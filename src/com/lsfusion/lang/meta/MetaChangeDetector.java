package com.lsfusion.lang.meta;

import com.intellij.codeInsight.completion.CompletionPhase;
import com.intellij.codeInsight.completion.CompletionPhaseListener;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Progressive;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.LowMemoryWatcher;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.cache.ModuleDependentsCache;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFMetaReference;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFFileUtils;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.intellij.codeInsight.completion.impl.CompletionServiceImpl.getCompletionPhase;

@Service(Service.Level.PROJECT)
public final class MetaChangeDetector extends PsiTreeChangeAdapter implements Disposable {

    private final Project myProject;
    public MetaChangeDetector(final Project project) {
        myProject = project;
    }

    public static MetaChangeDetector getInstance(Project project) {
        return project.getService(MetaChangeDetector.class);
    }

    private final static String ENABLED_META = "ENABLED_META";

    public void init() {
        PsiManager.getInstance(myProject).addPsiTreeChangeListener(this, this);

        DumbService.getInstance(myProject).smartInvokeLater(() -> {
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(myProject);
            setMetaEnabled(propertiesComponent.getBoolean(ENABLED_META, false), false);
        });

        myProject.getMessageBus().connect(this).subscribe(CompletionPhaseListener.TOPIC, (CompletionPhaseListener) isCompletionRunning -> checkCompletion());

        // Each cached list holds the metacode statements of every file that uses the declaration, and those are
        // AST-backed, so the parsed trees of those files cannot be released while the entry lives. Dropping the
        // whole cache when the IDE runs low on memory costs one usage search per declaration edited afterwards.
        LowMemoryWatcher.register(cacheUsages::clear, this);
    }

    // Plugin unload disposes the service but leaves the project alive, so myProject.isDisposed() cannot be
    // used to stop the asynchronous handoffs from resurrecting work (and retaining the plugin class loader).
    private volatile boolean disposed;

    @Override
    public void dispose() {
        disposed = true;
        ScheduledFuture<?> pending = pendingHeaderReprocess.getAndSet(null);
        if (pending != null)
            pending.cancel(false);
        pendingHeaderFiles.clear();
        processedHeaders.clear();
        cacheUsages.clear();
        // a runMetaText that died between inlinePend and the matching inlineProceed would leave the counters
        // unequal, keeping the progress loop in inlinePend spinning at its 200ms period (and the class loader
        // reachable) forever; with the counters equal the loop exits within a second
        synchronized (displaySync) {
            inlinePending = 0;
            inlineProceeded = 0;
        }
    }

    public static class MetaChangeListener implements ProjectActivity {
        @Override
        public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
            MetaChangeDetector.getInstance(project).init();
            return Unit.INSTANCE;
        }
    }

    private void checkCompletion() {
        CompletionPhase completionPhase = getCompletionPhase();
        boolean newCompletionRunning = !(completionPhase == CompletionPhase.NoCompletion || completionPhase instanceof CompletionPhase.ZombiePhase);
        if(newCompletionRunning || this.isCompletionRunning)
            lastCompletionRunning = System.currentTimeMillis();
        this.isCompletionRunning = newCompletionRunning;
    }

    private boolean isCompletionRunning;
    private long lastCompletionRunning;

    // fireChanged - чтобы отслеживать изменение сигнатуры в metacode declaration

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent event) {
        PsiElement element = event.getParent();
        if (checkProject(element)) {
            fireChanged(element);
            fireRemoved(event.getChild());
        }
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        PsiElement element = event.getParent();
        if (checkProject(element)) {
            fireChanged(event.getParent());
        }
    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {
        PsiElement element = event.getParent();
        if (checkProject(element)) {
            fireChanged(element);
            fireRemoved(event.getOldChild());
        }
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        PsiElement element = event.getParent();
        if (checkProject(element)) {
            fireChanged(element);
            fireAdded(event.getNewChild());
        }
    }

    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent event) {
        PsiElement element = event.getParent();
        if (checkProject(element)) {
            fireChanged(element);
        }
    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        PsiElement element = event.getParent();
        if (checkProject(element)) {
            fireChanged(element);
            fireAdded(event.getChild());
        }
    }

    //if we open more than one project with metacodes, each of them catch inlining metacode events for all opened projects
    private boolean checkProject(PsiElement element) {
        String projectPath = myProject.getBasePath();
        PsiFile file = element.getContainingFile();
        if(file != null) {
            VirtualFile virtualFile = file.getVirtualFile();
            if(virtualFile != null) {
                String filePath = virtualFile.getPath();
                return projectPath != null && filePath.contains(projectPath);
            }
        }
        return false;
    }

    private static String parseText(List<Pair<String, IElementType>> tokens, List<MetaTransaction.InToken> usages, List<String> decls, Set<String> metaDecls) {
        return new MetaCodeFragment(decls, metaDecls, tokens).getCode(usages);
    }

    public static List<String> getNewTokens(List<Pair<String, IElementType>> tokens, List<MetaTransaction.InToken> usages, List<String> decls, List<MetaTransaction.ExtToken> oldTokens) {
        return new MetaCodeFragment(decls, null, tokens).getNewTokens(usages, oldTokens);
    }

    private static int mapOffset(int offset, List<Pair<String, IElementType>> tokens, List<MetaTransaction.InToken> usages, List<String> decls, Set<String> metaDecls) {
        return new MetaCodeFragment(decls, metaDecls, tokens).mapOffset(offset, usages);
    }

    public static PsiElement mapOffset(PsiElement element) {

        PsiElement current = element;
        LSFMetaCodeBody metaBody;
        LSFMetaReference metaUsage;
        int offset = 0;
        while (true) {
            if (current == null || current instanceof LSFFile)
                return null;

            if (current instanceof LSFMetaCodeBody) {
                metaBody = (LSFMetaCodeBody) current;
                break;
            }

            offset += current.getStartOffsetInParent();
            current = current.getParent();
        }
        metaUsage = (LSFMetaReference) metaBody.getParent();

        if (!metaUsage.isCorrect())
            return null;

        LSFMetaDeclaration metaDecl = metaUsage.resolveDecl();
        if (metaDecl == null || !metaDecl.isCorrect())
            return null;

        int actualOffset = offset;

        for (LSFLazyMetaStatement lazyChild : metaBody.getLazyMetaStatementList()) {
            if (lazyChild.getStartOffsetInParent() >= actualOffset - 1) // -1 because of parenthesis
                break;
            for(LSFMetaCodeStatement child : lazyChild.getMetaCodeStatementList()) {
                LSFMetaCodeBody innerBody = child.getMetaCodeBody();
                if (innerBody != null) {
                    String text = innerBody.getText();
                    offset -= text.length();
                }
            }
        }

        return metaDecl.findOffsetInCode(mapOffset(offset, metaDecl.getMetaCode(), metaUsage.getUsageParams(), metaDecl.getDeclParams(), getMetaDecls(metaUsage)));
    }

    private static class LongLivingMeta {
        public final String name;
        public final int paramCount;
        public final LSFFile file;

        private LongLivingMeta(String name, int paramCount, LSFFile file) {
            this.name = name;
            this.paramCount = paramCount;
            this.file = file;
        }

        public boolean equals(Object o) {
            return this == o || o instanceof LongLivingMeta && paramCount == ((LongLivingMeta) o).paramCount && file.equals(((LongLivingMeta) o).file) && name.equals(((LongLivingMeta) o).name);
        }

        public int hashCode() {
            return 31 * (31 * name.hashCode() + paramCount) + file.hashCode();
        }
    }

    private static LongLivingMeta getLongLivingDecl(LSFMetaDeclaration decl) {
        return new LongLivingMeta(decl.getDeclName(), decl.getParamCount(), decl.getLSFFile());
    }

    private static class ToParse {

        private final List<Pair<String, IElementType>> tokens;
        private final List<MetaTransaction.InToken> usages;
        private final List<String> decls;
        public final long version;

        private final List<LSFMetaDeclaration> recursionGuard;
        private final Set<String> metaDecls;

        private ToParse(long version) {
            this(null, null, null, null, null, version);
        }
        private ToParse(List<Pair<String, IElementType>> tokens, List<MetaTransaction.InToken> usages, List<String> decls, Set<String> metaDecls, List<LSFMetaDeclaration> recursionGuard, long version) {
            this.tokens = tokens;
            this.usages = usages;
            this.decls = decls;
            this.version = version;
            this.metaDecls = metaDecls;
            this.recursionGuard = recursionGuard;
        }

        public LSFMetaCodeBody parse(LSFFile file, boolean untab) {
            if (tokens == null)
                return null;
            else {
                String text = parseText(tokens, usages, decls, metaDecls);
                if(untab) {
                    int nextLine = text.indexOf('\n');
                    if(nextLine >= 0) {
                        int i = nextLine +1;
                        String shiftString = "";
                        for(int size = text.length();i<size;i++) {
                            char charat = text.charAt(i);
                            if(charat == '\t' || charat == ' ')
                                shiftString += charat;
                            else {
                                if(charat == '\n')
                                    shiftString = "";
                                else
                                    break;
                            }
                        }
                        text = text.substring(i);
                        if(!shiftString.isEmpty())
                            text = text.replace("\n" + shiftString, "\n");
                    }

                }
                return LSFElementGenerator.createMetaBodyFromText(file, text, recursionGuard, metaDecls);
            }
        }
    }

    private static class GenParse {

        public final LSFMetaCodeStatement usage;
        public final LSFMetaCodeBody body;
        public final long version;

        private GenParse(LSFMetaCodeStatement usage, LSFMetaCodeBody body, long version) {
            this.usage = usage;
            this.body = body;
            this.version = version;
        }
    }

    private static long version;

    // без блокировок, так как во временном файле делается
    // дублирует код по сравнению с асинхронным, но обобщать их себе дороже
    // предполагается что usages из dumb файла
    
    public interface InlineProcessor {
        void proceed(Runnable inline);        
    }  
    public static void syncUsageProcessing(final LSFFile file, InlineProcessor inlineProcessor, ProgressIndicator indicator, boolean enabled, List<LSFMetaCodeStatement> usages, List<LSFMetaDeclaration> recursionGuard, Set<String> recMetaDecls) {
        int i=0;
        for (final LSFMetaCodeStatement metaUsage : usages)
            if (metaUsage.isCorrect()) {
                if(indicator != null)
                    indicator.setText2("Statements: " + (i++) + '/' + usages.size());

                final Result<ToParse> toParse = new Result<>();
                DumbService.getInstance(metaUsage.getProject()).runReadActionInSmartMode(() -> {
                    if (metaUsage.isCorrect()) {
                        LSFMetaDeclaration metaDecl;
                        if(enabled) {
                            LSFMetaReference reference;
                            if (inlineProcessor == null)
                                reference = LSFElementGenerator.createMetaRefFromText(metaUsage.getNameRef(), metaUsage.getFullNameRef(), file, metaUsage.getParamCount());
                            else
                                reference = metaUsage;
                            metaDecl = reference.resolveDecl();
                        } else {
                            metaDecl = null;
                        }

                        assert metaDecl == null || metaDecl.isValid();
                        if (metaDecl == null || !metaDecl.isCorrect() || recursionGuard.contains(metaDecl))
                            toParse.setResult(new ToParse(version));
                        else {
                            toParse.setResult(new ToParse(metaDecl.getMetaCode(), metaUsage.getUsageParams(), metaDecl.getDeclParams(), recMetaDecls == null ? getMetaDecls(metaUsage) : recMetaDecls, BaseUtils.add(recursionGuard, metaDecl), version));
                        }
                    }
                });

                if (toParse.getResult() != null) {
                    final LSFMetaCodeBody parsed = toParse.getResult().parse(file, false);
                    Runnable inlineRun = () -> metaUsage.setInlinedBody(parsed);
                    if(inlineProcessor != null)
                        inlineProcessor.proceed(inlineRun);
                    else
                        inlineRun.run();
                }
            }
    }

    private LinkedHashMap<Document, Long> changedDocs = new LinkedHashMap<>(16, 0.75f, true);
    private int inlinePending = 0;

    private void inlinePend(boolean sync) {
        synchronized (displaySync) {
            if (inlinePending == inlineProceeded) {
                if (!sync && !displayRunning) {
                    displayRunning = true;

                    final BackgroundableProcessIndicator indicator = new BackgroundableProcessIndicator(myProject, "Inlining metacode",  "cancel", "stop", false);
                    indicator.setIndeterminate(false);
                    ApplicationManager.getApplication().executeOnPooledThread(() -> ProgressManager.getInstance().runProcess(() -> {
                        assert displayRunning;

                        int idleTimes = 0;
                        while (true) {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ignored) {
                            }
                            synchronized (displaySync) {
                                if (inlinePending == inlineProceeded && idleTimes > 4) {
                                    displayRunning = false;
                                    return;
                                } else {
                                    idleTimes++;
                                    indicator.setFraction(inlinePending == 0 ? 1.0d : (double) inlineProceeded / (double) inlinePending);
                                    indicator.setText((lastProceeded != null ? "Last inlined: " + lastProceeded : "") + " " + inlineProceeded + "/" + inlinePending);
                                }
                            }
                        }
                    }, indicator));
                }
            }
            inlinePending++;
        }
    }

    private int inlineProceeded = 0;
    private String lastProceeded;

    private void inlineProceed(boolean easy, LSFFile file) {
        synchronized (displaySync) {
            if (easy)
                inlinePending--;
            else {
                inlineProceeded++;
                lastProceeded = file.getName();
            }

            if (inlinePending == inlineProceeded) {
                inlinePending = 0;
                inlineProceeded = 0;
            }
        }
    }

    private void inlinePostpone(final Runnable run, final boolean ui) {
        javax.swing.Timer timer = new javax.swing.Timer(200, evt -> {
            if (ui)
                ApplicationManager.getApplication().invokeLater(run);
            else
                ApplicationManager.getApplication().executeOnPooledThread(run);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private boolean displayRunning;
    private final Object displaySync = new Object();

    private boolean actual(GenParse gen) {
        return gen.version >= gen.usage.getVersion() && !usagesPending.processing.contains(gen.usage);
    }

    private void dropStaleGens(Map<LSFMetaCodeStatement, GenParse> gens, LSFFile file) {
        gens.values().removeIf(gen -> {
            boolean stale = !actual(gen);
            if (stale)
                inlineProceed(true, file);
            return stale;
        });
    }

    private static Set<String> getMetaDecls(LSFMetaReference metaCodeStatement) {
        LSFMetaCodeDeclarationStatement metaDecl = LSFReferenceAnnotator.getMetaDecl(metaCodeStatement);
        if(metaDecl != null)
            return new HashSet<>(metaDecl.getDeclParams());

        return Collections.emptySet();
    }
    
    public class MetaUsageProcessing implements Runnable {
        private LSFFile file;
        private Set<LSFMetaCodeStatement> usages;
        private boolean sync;
        private Boolean forcedEnabled;

        private MetaUsageProcessing(LSFFile file, Set<LSFMetaCodeStatement> usages, boolean sync, Boolean forcedEnabled) {
            this.file = file;
            this.usages = usages;
            this.sync = sync;
            this.forcedEnabled = forcedEnabled;
        }

        public void run() {
            if (disposed) // this reschedules itself through inlinePostpone below, so it has to stop explicitly
                return;

            final Map<LSFMetaCodeStatement, GenParse> genUsages = new HashMap<>();
            final List<LSFMetaCodeStatement> dumbRetry = new ArrayList<>();
            final Iterator<LSFMetaCodeStatement> iterator = usages.iterator();
            while (iterator.hasNext()) {
                final LSFMetaCodeStatement metaUsage = iterator.next();
                final Result<ToParse> toParse = new Result<>();
                final Result<Boolean> inline = new Result<>();
                ApplicationManager.getApplication().runReadAction(() -> {
                    version++; // синхронизация не волнует может быть и одна версия (если в рамках нескольких read'ов но не write'ов)
                    boolean keep = false;
                    if (metaUsage.isValid() && metaUsage.isCorrect()) {
                        if (!DumbService.isDumb(myProject)) {
                            inline.setResult(metaUsage.isInline());
                            LSFMetaDeclaration metaDecl = (forcedEnabled != null ? forcedEnabled : enabled) || inline.getResult() ? metaUsage.resolveDecl() : null;
                            assert metaDecl == null || metaDecl.isValid();
                            if (metaDecl == null || !declPending.processing.contains(getLongLivingDecl(metaDecl))) { // не обновляем, потому как все равно обновится при обработке metaDeclChanged
                                if (metaDecl == null || !metaDecl.isCorrect())
                                    toParse.setResult(new ToParse(version));
                                else
                                    toParse.setResult(new ToParse(metaDecl.getMetaCode(), metaUsage.getUsageParams(), metaDecl.getDeclParams(), getMetaDecls(metaUsage), Collections.emptyList(), version));
                            }
                        } else
                            keep = true;
                    }
                    if (!keep) {
                        metaUsage.setVersion(version);

                        boolean removed = usagesPending.processing.remove(metaUsage); // все равно плохо, изменение еще не "сохранено", а declPending уже обработаться и не найти этот usage
//                            assert removed;

                        iterator.remove();
                    }
                });

                // Parsing here, not inside the write action below, keeps that write action to pure tree
                // surgery - the parse of a whole batch used to run on the EDT - and a statement whose
                // expansion hits dumb mode (resolving a nested usage throws IndexNotReadyException) is
                // retried alone through the inlinePostpone at the end, instead of aborting the write action
                // and silently losing the batch.
                if (toParse.getResult() != null) {
                    try {
                        LSFMetaCodeBody body = ApplicationManager.getApplication().runReadAction(
                                (Computable<LSFMetaCodeBody>) () -> toParse.getResult().parse(file, inline.getResult()));
                        genUsages.put(metaUsage, new GenParse(metaUsage, body, toParse.getResult().version));
                    } catch (IndexNotReadyException e) {
                        dumbRetry.add(metaUsage);
                    }
                }
            }
            // back into processing as well, not just into usages: the read action above already took these out of
            // it, and without that an addUsageProcessing arriving before the retry would queue a second run for the
            // same statement (the dumb path keeps them in processing by not removing them at all)
            usagesPending.processing.addAll(dumbRetry);
            usages.addAll(dumbRetry);
            // Every applied body is a physical PSI change, which restarts the daemon and drops every PsiDependentCache
            // in the plugin. Applying the whole batch of a file in one write action instead of one per statement keeps
            // that cost proportional to the number of edited files rather than to the number of metacode usages.
            if (!genUsages.isEmpty()) {
                PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(myProject);
                final Document document = psiDocumentManager.getDocument(file);

                final Result<Runnable> runMetaText = new Result<>();
                runMetaText.setResult(() -> {
                    dropStaleGens(genUsages, file); // оптимизация
                    if (genUsages.isEmpty())
                        return;

                    // без perform for commited постоянно рассинхронизируется дерево с текстом
                    psiDocumentManager.performForCommittedDocument(document, () -> {
                        dropStaleGens(genUsages, file); // оптимизация
                        if (genUsages.isEmpty())
                            return;

                        if (reprocessing) {
                            inlinePostpone(runMetaText.getResult(), true);
                            return;
                        }

                        if (!sync) { // в синхронном режиме не нужны задержки
                            long current = System.currentTimeMillis();

                            long lastTime;
                            if (!(changedDocs.size() < 2 || changedDocs.containsKey(document))) {
                                Map.Entry<Document, Long> lastChanged = changedDocs.entrySet().iterator().next();
                                lastTime = lastChanged.getValue();
                                long timeElapsed = current - lastTime;
                                if (timeElapsed <= 200) {
                                    inlinePostpone(runMetaText.getResult(), true);
                                    return;
                                }
                                changedDocs.remove(lastChanged.getKey());
                            }
                            changedDocs.put(document, current);
                        }

                        for (int i = 0; i < genUsages.size(); i++)
                            inlineProceed(false, file);

                        try {
                            CommandProcessor.getInstance().runUndoTransparentAction(() -> ApplicationManager.getApplication().runWriteAction(() -> {
                                boolean prevEnabled = enabled;
                                enabled = false; // выключаем чтобы каскадно не вызывались события
                                try {
                                    for (GenParse gen : genUsages.values())
                                        if (gen.usage.isValid() && gen.usage.isCorrect() && actual(gen)) // can become not valid
                                            gen.usage.setInlinedBody(gen.body);
                                } finally {
                                    enabled = prevEnabled;
                                }
                            }));
                        } catch(IndexNotReadyException e) {
                            // with the bodies parsed up front nothing here should touch an index anymore, but if
                            // some path still does, re-queue the batch - run() has long returned by this point,
                            // so adding back to usages would be dead
                            addUsageProcessing(new ArrayList<>(genUsages.keySet()));
                        }
                    });
                });

                ApplicationManager.getApplication().invokeLater(new CompletionRunner(file) {
                    @Override
                    public void runNoCompletion() {
                        genUsages.values().removeIf(gen -> !actual(gen)); // оптимизация, до inlinePend, чтобы не сбивать счетчик
                        if (genUsages.isEmpty())
                            return;

                        for (int i = 0; i < genUsages.size(); i++)
                            inlinePend(sync);
                        runMetaText.getResult().run();
                    }
                });
            }

            if (!usages.isEmpty())
                inlinePostpone(this, false); // еще раз запустим, так как некоторые в Dumb выполнялись
        }
    }

    private abstract class CompletionRunner implements Runnable {

        private final LSFFile file;

        public CompletionRunner(LSFFile file) {
            this.file = file;
        }

        public abstract void runNoCompletion();

        @Override
        public void run() {
            if (disposed) // the postpone below reschedules this runnable, so it has to stop explicitly
                return;

            // the problem here is that changing editing document stops completion (expires document), so if we see that there is a completion running, we postpone the inlining
            if (isRunningCompletion(file))
                inlinePostpone(this, true);
            else
                runNoCompletion();
        }
    }

    private boolean isRunningCompletion(LSFFile file) {
        checkCompletion();
        // because completion can stop and start right away we add some delay
        if(isCompletionRunning || System.currentTimeMillis() - lastCompletionRunning < 1000) {
            FileEditor selectedEditor = FileEditorManager.getInstance(myProject).getSelectedEditor();
            if(selectedEditor != null) {
                VirtualFile editingFile = selectedEditor.getFile();
                return editingFile != null && editingFile.equals(file.getVirtualFile());
            }
        }
        return false;
    }

    private abstract class MetaPending<T, G> {
        public final Set<Object> processing = ContainerUtil.newConcurrentSet();
        private Map<G, Set<T>> pending = new HashMap<>();

        protected abstract G group(T element);

        protected abstract Runnable createAction(G group, Set<T> elements);

        private boolean flushing = false;

        private void flushAll() {
            for (Map.Entry<G, Set<T>> group : pending.entrySet()) {
                processing.addAll(group.getValue());
                ApplicationManager.getApplication().executeOnPooledThread(createAction(group.getKey(), group.getValue()));
            }
            pending = new HashMap<>();
        }

        private void flushGroup(G group) {
            Set<T> flush = pending.remove(group);
            processing.addAll(flush);
            ApplicationManager.getApplication().executeOnPooledThread(createAction(group, flush));
        }

        private final Runnable flush = new Runnable() {
            public void run() {
                synchronized (this) {
                    assert flushing;
                    flushAll();
                    flushing = false;
                }
            }
        };

        protected boolean extraCheck(T element) {
            return true;
        }

        // Only a safety valve for pathological bursts: the flush below is scheduled right away anyway, so flushing
        // early just splits what would have been one batch (= one write action per file) into many tiny ones.
        private static final int MAX_PENDING_PER_GROUP = 500;
        private static final int MAX_PENDING_GROUPS = 50;

        public void add(Collection<T> elements) {
            if (disposed)
                return;
            for (T statement : elements) {
                if (!processing.contains(statement) && extraCheck(statement)) {
                    synchronized (flush) {
                        G group = group(statement);
                        Set<T> pendEls = pending.computeIfAbsent(group, k -> new HashSet<>());
                        pendEls.add(statement);
                        if (pendEls.size() > MAX_PENDING_PER_GROUP)
                            flushGroup(group);
                        if (pending.size() > MAX_PENDING_GROUPS)
                            flushAll();
                        if (!flushing) {
                            flushing = true;
                            ApplicationManager.getApplication().executeOnPooledThread(flush);
                        }
                    }
                }
            }
        }

        public void add(T element) {
            add(Collections.singleton(element));
        }
    }

    private class MetaUsagesPending extends MetaPending<LSFMetaCodeStatement, LSFFile> {
        private MetaUsagesPending() {
        }

        @Override
        protected boolean extraCheck(LSFMetaCodeStatement element) {
            return element.getContainingFile() instanceof LSFFile; // почему то DummyHolder'ы попадают
        }

        protected LSFFile group(LSFMetaCodeStatement element) {
            return element.getLSFFile();
        }

        protected Runnable createAction(LSFFile file, Set<LSFMetaCodeStatement> elements) {
            return new MetaUsageProcessing(file, elements, false, null);
        }
    }

    private final MetaUsagesPending usagesPending = new MetaUsagesPending();

    private void addUsageProcessing(Collection<LSFMetaCodeStatement> used) { // в синхронном режиме может вызываться должен быть достаточно быстрым
//        assert enabled;
        usagesPending.add(used);
    }

    private void addForcedUsageProcessing(LSFFile file, List<LSFMetaCodeStatement> used, Boolean forcedEnabled) { // в синхронном режиме может вызываться должен быть достаточно быстрым
        usagesPending.processing.addAll(used);
        new MetaUsageProcessing(file, new HashSet<>(used), true, forcedEnabled).run();
    }

    private void addUsageProcessing(LSFMetaCodeStatement statement) { // в синхронном режиме может вызываться должен быть достаточно быстрым
        addUsageProcessing(Collections.singleton(statement));
    }

    private ConcurrentMap<LongLivingMeta, List<LSFMetaCodeStatement>> cacheUsages = new ConcurrentHashMap<>();


    // Each entry here is a project-wide usage search (findMetaUsages), hundreds of milliseconds. The whole cache used
    // to be dropped on any change outside a metacode body - in practice on every keystroke - so the search was
    // repeated for every burst of typing in a file that declares metacodes. A cached list is only dangerous when it
    // misses a live usage: an extra statement is re-resolved when the list is processed, and a dead one is dropped
    // by the isValid check when the list is read. A usage of a name can appear through the subtree of an add/remove
    // event (changedUsages, collected at every depth by collectMetaStatements), through an in-place edit of the
    // header of an enclosing usage (the ancestor walk below - renaming @foo to @bar, or changing its arity,
    // replaces a leaf deep inside the statement), or through a REQUIRE change re-targeting usages to other
    // declarations wholesale (fireChangedModuleHeader).
    private void invalidateCachedUsages(PsiElement element, Set<String> changedUsages) {
        if (cacheUsages.isEmpty())
            return;

        // starting at the element itself, not at its parent: the body we inline is replaced as a whole, and that
        // event carries the body as the element - walking from its parent would attribute our own inlining to the
        // enclosing usage and drop the very list the inlining was started from
        for (PsiElement parent = element; parent != null && !(parent instanceof PsiFile); parent = parent.getParent()) {
            if (parent instanceof LSFMetaCodeBody || parent instanceof LSFMetaCodeDeclBody)
                break; // a change inside a body cannot touch the header of the enclosing statement
            if (parent instanceof LSFMetaCodeStatement) {
                addChangedUsage(parent, changedUsages);
                break;
            }
        }

        if (!changedUsages.isEmpty())
            cacheUsages.keySet().removeIf(decl -> changedUsages.contains(decl.name));
    }

    private static void addChangedUsage(PsiElement element, Set<String> changedUsages) {
        if (element instanceof LSFMetaCodeStatement usage && usage.isCorrect()) {
            String name = usage.getNameRef();
            if (name != null)
                changedUsages.add(name);
        }
    }

    // One walk of an added/removed subtree serving both consumers. Reprocessing (statements) wants only the
    // outermost meta statements: re-inlining a usage regenerates its nested usages recursively, and
    // addDeclProcessing reprocesses the usages inside the declaration body itself. Cache invalidation
    // (changedUsages) needs the names from every depth: the cached lists come from a text search, so they also
    // hold usages nested inside inlined bodies.
    private static void collectMetaStatements(PsiElement element, boolean outermost, List<PsiElement> statements, Set<String> changedUsages) {
        boolean statement = element instanceof LSFMetaCodeStatement || element instanceof LSFMetaCodeDeclarationStatement;
        if (statement) {
            if (outermost)
                statements.add(element);
            addChangedUsage(element, changedUsages);
        }
        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling())
            collectMetaStatements(child, outermost && !statement, statements, changedUsages);
    }

    void fireChangedModuleHeader(LSFFile file) {
        LSFGlobalResolver.cached.clear(); // убираем все, потому как могут быть зависимости
        // the cached lists are REQUIRE-scope dependent (findMetaUsages keeps only the usages that resolve to the
        // declaration), so a header change can re-target any usage in the project to another declaration
        cacheUsages.clear();

        if (enabled && file != null)
            scheduleModuleHeaderUsageProcessing(file);
    }

    // Re-inlining the metacode of the whole REQUIRE closure is far too expensive to do per keystroke - and the header
    // is edited character by character, each character producing two PSI events. So the reprocessing is postponed
    // until the typing stops, and then skipped altogether if the header text ended up the same as the last one we
    // processed (typing that was undone, reformatting, whitespace).
    private static final int HEADER_REPROCESS_DELAY = 700;

    private final Set<LSFFile> pendingHeaderFiles = ContainerUtil.newConcurrentSet();
    private final Map<LSFFile, String> processedHeaders = ContainerUtil.createConcurrentWeakMap();
    private final AtomicReference<ScheduledFuture<?>> pendingHeaderReprocess = new AtomicReference<>();

    private void scheduleModuleHeaderUsageProcessing(LSFFile file) {
        pendingHeaderFiles.add(file);
        // the scheduler is only used to count down the delay - reprocessChangedModuleHeaders waits for smart mode,
        // which during indexing means minutes, and the scheduler's threads are shared with the rest of the IDE
        ScheduledFuture<?> previous = pendingHeaderReprocess.getAndSet(AppExecutorUtil.getAppScheduledExecutorService()
                .schedule(() -> ApplicationManager.getApplication().executeOnPooledThread(this::reprocessChangedModuleHeaders),
                        HEADER_REPROCESS_DELAY, TimeUnit.MILLISECONDS));
        if (previous != null)
            previous.cancel(false);
    }

    private void reprocessChangedModuleHeaders() {
        if (disposed || myProject.isDisposed())
            return;

        DumbService dumbService = DumbService.getInstance(myProject);
        // don't park a shared pool worker for the whole indexing (runReadActionInSmartMode blocks, and every
        // overlapping invocation would stack another blocked worker) - runWhenSmart holds no thread
        if (dumbService.isDumb()) {
            dumbService.runWhenSmart(() -> ApplicationManager.getApplication().executeOnPooledThread(this::reprocessChangedModuleHeaders));
            return;
        }

        // draining instead of copying and clearing: a file added in between would be dropped without being processed
        for (Iterator<LSFFile> iterator = pendingHeaderFiles.iterator(); iterator.hasNext(); ) {
            LSFFile file = iterator.next();
            iterator.remove();
            try {
                ApplicationManager.getApplication().runReadAction(() -> {
                    if (!file.isValid() || !enabled)
                        return;

                    // the module declaration is the module header (the moduleHeader rule carries both the
                    // LSFModuleDeclaration mixin and the MODULE stub type), so this is at once the text to compare
                    // and the root of the REQUIRE closure below
                    LSFModuleDeclaration module = file.getModuleDeclaration();
                    String headerText = module == null ? "" : module.getText();
                    if (headerText.equals(processedHeaders.get(file)))
                        return;

                    addUsageProcessing(file.getMetaCodeStatementList());
                    if (module != null && module.isValid())
                        addDependentModulesUsageProcessing(module, ContainerUtil.newHashSet(module));

                    // recorded only once the whole closure is queued, so a failure above leaves the header pending
                    processedHeaders.put(file, headerText);
                });
            } catch (IndexNotReadyException e) {
                // indexing started between the isDumb check and the read action; the undrained files would hit
                // it too, so put this one back and retry them all together after the indexing ends
                pendingHeaderFiles.add(file);
                dumbService.runWhenSmart(() -> ApplicationManager.getApplication().executeOnPooledThread(this::reprocessChangedModuleHeaders));
                return;
            }
        }
    }

    private void addDependentModulesUsageProcessing(LSFModuleDeclaration module, Set<LSFModuleDeclaration> proceeded) {
        List<LSFModuleDeclaration> dependentModules = module.getRequireModules();//ModuleDependentsCache.getInstance(myProject).resolveWithCaching(module);
        for (LSFModuleDeclaration dependent : dependentModules) {
            if (dependent.isValid() && proceeded.add(dependent)) {
                LSFFile dependentFile = dependent.getLSFFile();
                if (dependentFile != null && dependentFile.isValid())
                    addUsageProcessing(dependentFile.getMetaCodeStatementList());
                addDependentModulesUsageProcessing(dependent, proceeded);
            }
        }
    }

    private class MetaDeclProcessing implements Runnable {
        private Set<LongLivingMeta> decls;

        private MetaDeclProcessing(Set<LongLivingMeta> decls) {
            this.decls = decls;
        }

        public void run() {
            DumbService dumbService = DumbService.getInstance(myProject);
            for (final LongLivingMeta metaDecl : decls) {
                dumbService.runReadActionInSmartMode(() -> {
                    List<LSFMetaCodeStatement> usages = null;
                    if (metaDecl.file.isValid()) {
                        usages = cacheUsages.get(metaDecl);
                        if (usages != null && !ContainerUtil.all(usages, PsiElement::isValid))
                            usages = null; // a file can be reparsed as a whole without any add/remove event we could see
                        if (usages == null) {
                            usages = LSFResolver.findMetaUsages(metaDecl.name, metaDecl.paramCount, metaDecl.file);
                            cacheUsages.put(metaDecl, usages);
                        }
                    }
                    boolean removed = declPending.processing.remove(metaDecl);
                    assert removed;

                    if (usages != null)
                        addUsageProcessing(usages);
                });
            }
        }
    }

    private class MetaDeclPending extends MetaPending<LongLivingMeta, Integer> {
        private MetaDeclPending() {
        }

        protected Integer group(LongLivingMeta element) {
            return 0;
        }

        protected Runnable createAction(Integer group, Set<LongLivingMeta> elements) {
            return new MetaDeclProcessing(elements);
        }
    }

    private final MetaDeclPending declPending = new MetaDeclPending();

    private void addDeclProcessing(LSFMetaCodeDeclarationStatement decl) {
        assert ApplicationManager.getApplication().isWriteAccessAllowed();
        if (!decl.isCorrect())
            return;

        declPending.add(getLongLivingDecl(decl));

        // since there is a problem with change detector we have to reprocess all meta usages inside the decl (overhead is not that huge right now)
        LSFMetaCodeDeclBody metaCodeDeclBody = decl.getMetaCodeDeclBody();
        if(metaCodeDeclBody != null)
            for (LSFLazyMetaDeclStatement metaDeclStatement : metaCodeDeclBody.getLazyMetaDeclStatementList())
                addUsageProcessing(metaDeclStatement.getMetaCodeStatementList());
    }

    private boolean reprocessing = false;

    private boolean enabled = false;

    public void toggleMetaEnabled(List<String> modulesToInclude) {
        setMetaEnabled(modulesToInclude, !enabled, true);
    }

    public void setMetaEnabled(boolean enabled, boolean reprocess) {
        setMetaEnabled(null, enabled, reprocess);
    }

    public void setMetaEnabled(List<String> modulesToInclude, boolean enabled, boolean reprocess) {
        this.enabled = enabled;
        if (!enabled)
            cacheUsages.clear(); // don't pin project-wide statement lists while disabled; also unlocks the fast path in fireRemoved
        PropertiesComponent.getInstance(myProject).setValue(ENABLED_META, Boolean.toString(enabled));

        if (reprocess)
            reprocessAllDocuments(modulesToInclude, false);
    }

    public boolean getMetaEnabled() {
        return enabled;
    }

    public void reprocessFile(LSFFile file, boolean enabled) {
        addForcedUsageProcessing(file, file.getMetaCodeStatementList(), enabled);
    }

    // Synchronous show/hide for one file, for MCP tool calls: unlike reprocessFile (fire-and-forget
    // via invokeLater, meant for the "Show/Hide Meta for File" editor actions), this blocks until the
    // edit is actually applied and explicitly saves the document - needed because there may be no open
    // editor/autosave to flush the change to disk, and the caller (an AI agent doing e.g. svn diff or
    // a commit right after) needs the result to already be on disk when the call returns.
    public void reprocessFileForMcp(LSFFile file, boolean show) {
        List<Runnable> inlines = new ArrayList<>();
        InlineProcessor collector = inlines::add;

        ApplicationManager.getApplication().runReadAction(() -> {
            List<LSFMetaCodeStatement> statements = show ? file.getDisabledMetaCodeStatementList() : file.getMetaCodeStatementList();
            syncUsageProcessing(file, collector, null, show, statements, Collections.emptyList(), null);
        });

        if (inlines.isEmpty())
            return;

        ApplicationManager.getApplication().invokeAndWait(() ->
            CommandProcessor.getInstance().runUndoTransparentAction(() -> ApplicationManager.getApplication().runWriteAction(() -> {
                boolean prevEnabled = enabled;
                enabled = false; // suppress cascading reprocessing from our own PSI change events (see fireChanged)
                try {
                    for (Runnable inline : inlines)
                        inline.run();
                } finally {
                    enabled = prevEnabled;
                }

                Document document = PsiDocumentManager.getInstance(myProject).getDocument(file);
                if (document != null && FileDocumentManager.getInstance().isDocumentUnsaved(document))
                    FileDocumentManager.getInstance().saveDocumentAsIs(document);
            })));
    }

    public void reprocessAllDocuments() {
        reprocessAllDocuments(null, false);
    }
    
    public void reenableAllMetaCodes() {
        reprocessAllDocuments(null, true);
    }
    
    private class ReprocessInlineProcessor implements InlineProcessor {
        private final ProgressIndicator indicator;

        public ReprocessInlineProcessor(ProgressIndicator indicator) {
            this.indicator = indicator;
        }

        private final List<Runnable> postponed = new ArrayList<>();

        public void proceed(Runnable inline) {
            postponed.add(inline);
        }
        
        public void checkAndFlushPostponed() {
            if(postponed.size() > 50000)
                flushPostponed();
        }
        
        private void runEDTWriteUndo(Runnable run) {
            Runnable flush = () -> CommandProcessor.getInstance().runUndoTransparentAction(() -> ApplicationManager.getApplication().runWriteAction(run));
            if(ApplicationManager.getApplication().isDispatchThread())
                flush.run();
            else
                ApplicationManager.getApplication().invokeAndWait(flush);
        }

        public void flushPostponed() {
            if(postponed.isEmpty())
                return;
            
            boolean prevEnabled = enabled;
            enabled = false;
            try {
                int blockSize = 500;
                int blocks = (postponed.size() - 1) / blockSize + 1;
                for(int i=0;i<blocks;i++) {
                    indicator.setText2("Inlining meta code body: " + i + "/" + blocks);
                    final int fi = i;
                    runEDTWriteUndo(() -> {
                        for(int j=fi*blockSize;j<BaseUtils.min((fi+1)*blockSize, postponed.size());j++) {
                            postponed.get(j).run();
                        }
                        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
                        for (Document document : fileDocumentManager.getUnsavedDocuments()) {
                            fileDocumentManager.saveDocumentAsIs(document);
                        }
                    });
                }
                indicator.setText2("");
            } finally {
                enabled = prevEnabled;
            }
            postponed.clear();
        }
    }

    public void reprocessAllDocuments(List<String> modulesToInclude, final boolean reenable) {
        final Progressive run = indicator -> {
            reprocessing = true;

            GlobalSearchScope searchScope = LSFFileUtils.getScope(modulesToInclude, myProject);

            List<LSFFile> lsfFiles = ApplicationManager.getApplication().runReadAction((Computable<List<LSFFile>>) () -> getLsfFiles(searchScope));

            ReprocessInlineProcessor inlineProcessor = new ReprocessInlineProcessor(indicator);

            int i = 0;
            for (LSFFile lsfFile : lsfFiles) {
                indicator.setText("Processing: " + lsfFile.getName());

                ApplicationManager.getApplication().runReadAction(() -> {
                    List<LSFMetaCodeStatement> metaStatements = reenable ? lsfFile.getDisabledMetaCodeStatementList() : lsfFile.getMetaCodeStatementList();
                    syncUsageProcessing(lsfFile, inlineProcessor, indicator, enabled, metaStatements, Collections.emptyList(), null);
                    indicator.setText2("");
                });

                inlineProcessor.checkAndFlushPostponed();

                indicator.setFraction(((double) i++) / lsfFiles.size());
            }

            inlineProcessor.flushPostponed();

            reprocessing = false;
        };

        Task task = new Task.Modal(myProject, "Updating metacode", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                run.run(indicator);
            }
        };
        ProgressManager.getInstance().run(task);
    }

    private List<LSFFile> getLsfFiles(GlobalSearchScope searchScope) {
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(LSFFileType.INSTANCE, searchScope);
        List<LSFFile> lsfFiles = new ArrayList<>();
        for(VirtualFile virtualFile : virtualFiles) {
            if(FileStatusManager.getInstance(myProject).getStatus(virtualFile) != FileStatus.IGNORED) {
                PsiFile psiFile = PsiUtilBase.getPsiFile(myProject, virtualFile);
                if(psiFile instanceof LSFFile)
                    lsfFiles.add((LSFFile) psiFile);
            }
        }
        return lsfFiles;
    }

    private void fireChanged(PsiElement element) {
        // this whole thing doesn't actually work because of lazy elements (see comment in LSF.bnf)
        boolean inMetaBody = false;
        boolean inModuleHeader = false;
        PsiFile containingFile = element.getContainingFile();
        LSFFile lsfFile = containingFile instanceof LSFFile ? (LSFFile) containingFile : null;
        while (element != null && !(element instanceof LSFFile)) {
            if (enabled) {
                if (element instanceof LSFMetaCodeDeclarationStatement && !inMetaBody) { // if we changed something in meta code body, it's not considered meta decl change
                    addDeclProcessing((LSFMetaCodeDeclarationStatement) element);
                }

                if (element instanceof LSFMetaCodeBody)
                    inMetaBody = true;
            }
                
            if (element instanceof LSFMetaCodeStatement && (enabled || ((LSFMetaCodeStatement) element).isInline())) {
                addUsageProcessing((LSFMetaCodeStatement) element);
            }

            if (element instanceof LSFModuleHeader) {
                inModuleHeader = true;
            }

            element = element.getParent();
        }

        if (inModuleHeader) {
            fireChangedModuleHeader(lsfFile);
        }
    }

    private void fireAdded(PsiElement element) {
        List<PsiElement> statements = new ArrayList<>();
        Set<String> changedUsages = new HashSet<>();
        collectMetaStatements(element, true, statements, changedUsages);
        invalidateCachedUsages(element, changedUsages);

        for (PsiElement child : statements) {
            if (child instanceof LSFMetaCodeStatement && (enabled || ((LSFMetaCodeStatement) child).isInline())) {
                // нужно перегенерировать тело использования
                addUsageProcessing((LSFMetaCodeStatement) child);
            } else if (enabled && child instanceof LSFMetaCodeDeclarationStatement) {
                // нужно перегенерить все usage'ы этого метакода
                addDeclProcessing((LSFMetaCodeDeclarationStatement) child);
            }
        }
    }

    private void fireRemoved(PsiElement element) {
        if (!enabled && cacheUsages.isEmpty())
            return;

        List<PsiElement> statements = new ArrayList<>();
        Set<String> changedUsages = new HashSet<>();
        collectMetaStatements(element, true, statements, changedUsages);
        invalidateCachedUsages(element, changedUsages);

        if (!enabled)
            return;

        for (PsiElement child : statements) {
            if (child instanceof LSFMetaCodeDeclarationStatement) {
                // нужно перегенерить все usage'ы этого метакода
                addDeclProcessing((LSFMetaCodeDeclarationStatement) child);
            }
        }
    }
}
