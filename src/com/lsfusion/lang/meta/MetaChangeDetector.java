package com.lsfusion.lang.meta;

import com.intellij.codeInsight.completion.CompletionPhase;
import com.intellij.codeInsight.completion.CompletionPhaseListener;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.references.LSFMetaReference;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFFileUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static com.intellij.codeInsight.completion.impl.CompletionServiceImpl.getCompletionPhase;
import static com.lsfusion.util.LSFPsiUtils.findChildrenOfType;

public class MetaChangeDetector extends PsiTreeChangeAdapter implements ProjectComponent {
    private final PsiManager myPsiManager;
    private final FileEditorManager myFileEditorManager;
    private final Project myProject;
    private final PsiDocumentManager myPsiDocumentManager;

    public MetaChangeDetector(
            final PsiDocumentManager psiDocumentManager,
            final PsiManager psiManager,
            final FileEditorManager fileEditorManager,
            final Project project
    ) {
        myPsiDocumentManager = psiDocumentManager;
        myPsiManager = psiManager;
        myFileEditorManager = fileEditorManager;
        myProject = project;
    }

    public static MetaChangeDetector getInstance(Project project) {
        return project.getComponent(MetaChangeDetector.class);
    }

    private Timer timer;
    private final static String ENABLED_META = "ENABLED_META";

    @Override
    public void projectOpened() {
        myPsiManager.addPsiTreeChangeListener(this);

        DumbService.getInstance(myProject).smartInvokeLater(
                new Runnable() {
                    public void run() {
                        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(myProject);
                        setMetaEnabled(propertiesComponent.getBoolean(ENABLED_META, false), false);
                    }
                }
        );

        myProject.getMessageBus().connect().subscribe(CompletionPhaseListener.TOPIC, isCompletionRunning -> {
            checkCompletion();
        });
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

    @Override
    public void projectClosed() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "MetaChangeDetector";
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

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

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
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
        public final ToParse toParse;
        public final long version;

        private GenParse(LSFMetaCodeStatement usage, ToParse toParse, long version) {
            this.usage = usage;
            this.toParse = toParse;
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
                    indicator.setText2("Statements : " + (i++) + '\\' + usages.size());

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

                    final BackgroundableProcessIndicator indicator = new BackgroundableProcessIndicator(myProject, "Inlining metacode", PerformInBackgroundOption.ALWAYS_BACKGROUND,  "cancel", "stop", false);
                    indicator.setIndeterminate(false);
                    ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                        public void run() {
                            ProgressManager.getInstance().runProcess(new Runnable() {
                                public void run() {
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
                                                indicator.setText((lastProceeded != null ? "Last inlined : " + lastProceeded : "") + " " + inlineProceeded + "/" + inlinePending);
                                            }
                                        }
                                    }
                                }
                            }, indicator);
                        }
                    });
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
        javax.swing.Timer timer = new javax.swing.Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (ui)
                    ApplicationManager.getApplication().invokeLater(run);
                else
                    ApplicationManager.getApplication().executeOnPooledThread(run);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private boolean displayRunning;
    private final Object displaySync = new Object();

    private boolean actual(GenParse gen) {
        return gen.version >= gen.usage.getVersion() && !usagesPending.processing.contains(gen.usage);
    }

    private boolean actualize(GenParse gen, LSFFile file) {
        boolean result = actual(gen);
        if (!result)
            inlineProceed(true, file);
        return result;
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
            final List<GenParse> genUsages = new ArrayList<>();
            final Iterator<LSFMetaCodeStatement> iterator = usages.iterator();
            while (iterator.hasNext()) {
                final LSFMetaCodeStatement metaUsage = iterator.next();
                final Result<ToParse> toParse = new Result<>();
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    public void run() {
                        version++; // синхронизация не волнует может быть и одна версия (если в рамках нескольких read'ов но не write'ов)
                        boolean keep = false;
                        if (metaUsage.isValid() && metaUsage.isCorrect()) {
                            if (!DumbService.isDumb(myProject)) {
                                LSFMetaDeclaration metaDecl = (forcedEnabled != null ? forcedEnabled : enabled) || metaUsage.isInline() ? metaUsage.resolveDecl() : null;
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
                    }
                });

                if (toParse.getResult() != null)
                    genUsages.add(new GenParse(metaUsage, toParse.getResult(), toParse.getResult().version));
            }
            final Document document = myPsiDocumentManager.getDocument(file);
            for (final GenParse gen : genUsages) {
                final Result<Runnable> runMetaText = new Result<>();
                runMetaText.setResult(new Runnable() {
                    public void run() {
                        if (!actualize(gen, file)) // оптимизация
                            return;

                        myPsiDocumentManager.performForCommittedDocument(document, new Runnable() { // без perform for commited постоянно рассинхронизируется дерево с текстом
                            public void run() {
                                if (!actualize(gen, file)) // оптимизация
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

                                inlineProceed(false, file);

                                CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                                    public void run() {
                                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                            public void run() {
                                                if (gen.usage.isValid() && gen.usage.isCorrect() && actual(gen)) { // can become not valid
                                                    boolean prevEnabled = enabled;
                                                    enabled = false; // выключаем чтобы каскадно не вызывались события
                                                    try {
                                                        gen.usage.setInlinedBody(gen.toParse.parse(file, gen.usage.isInline()));
                                                    } finally {
                                                        enabled = prevEnabled;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

                Runnable runGenUsage = new Runnable() {
                    public void run() {
                        if (!actual(gen)) // оптимизация
                            return;

                        inlinePend(sync);
                        runMetaText.getResult().run();
                    }
                };

                ApplicationManager.getApplication().invokeLater(new CompletionRunner(file) {
                    @Override
                    public void runNoCompletion() {
                        runGenUsage.run();
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
            FileEditor selectedEditor = myFileEditorManager.getSelectedEditor();
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

        public void add(Collection<T> elements) {
            for (T statement : elements) {
                if (!processing.contains(statement) && extraCheck(statement)) {
                    synchronized (flush) {
                        G group = group(statement);
                        Set<T> pendEls = pending.get(group);
                        if (pendEls == null) {
                            pendEls = new HashSet<>();
                            pending.put(group, pendEls);
                        }
                        pendEls.add(statement);
                        if (pendEls.size() > 4)
                            flushGroup(group);
                        if (pending.size() > 4)
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

    private ConcurrentMap<LongLivingMeta, List<LSFMetaCodeStatement>> cacheUsages = ContainerUtil.newConcurrentMap();


    void fireChangedNotMetaBody() {
        cacheUsages.clear();
    }

    void fireChangedModuleHeader() {
        LSFGlobalResolver.cached.clear(); // убираем все, потому как могут быть зависимости
    }

    private class MetaDeclProcessing implements Runnable {
        private Set<LongLivingMeta> decls;

        private MetaDeclProcessing(Set<LongLivingMeta> decls) {
            this.decls = decls;
        }

        public void run() {
            if (!DumbService.isDumb(myProject)) {
                for (final LongLivingMeta metaDecl : decls) {
                    DumbService.getInstance(myProject).smartInvokeLater(new Runnable() {
                        public void run() {
                            List<LSFMetaCodeStatement> usages = null;
                            if (metaDecl.file.isValid()) {
                                usages = cacheUsages.get(metaDecl);
                                if (usages == null) {
                                    usages = LSFResolver.findMetaUsages(metaDecl.name, metaDecl.paramCount, metaDecl.file);
                                    cacheUsages.put(metaDecl, usages);
                                }
                            }
                            boolean removed = declPending.processing.remove(metaDecl);
                            assert removed;

                            if (usages != null)
                                addUsageProcessing(usages);
                        }
                    });
                }
            } else
                inlinePostpone(this, false);
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
                    indicator.setText2("Inlining meta code body : " + i + "/" + blocks);
                    final int fi = i;
                    runEDTWriteUndo(() -> {
                        for(int j=fi*blockSize;j<BaseUtils.min((fi+1)*blockSize, postponed.size());j++) {
                            postponed.get(j).run();
                        }
                        FileDocumentManager.getInstance().saveAllDocuments();
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
                indicator.setText("Processing : " + lsfFile.getName());

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
        boolean inMetaDeclBody = false;
        boolean inModuleHeader = false;
        while (element != null && !(element instanceof LSFFile)) {
            if (enabled) {
                if (element instanceof LSFMetaCodeDeclarationStatement && !inMetaBody) { // if we changed something in meta code body, it's not considered meta decl change
                    addDeclProcessing((LSFMetaCodeDeclarationStatement) element);
                }

                if (element instanceof LSFMetaCodeBody)
                    inMetaBody = true;
                if (element instanceof LSFMetaCodeDeclBody)
                    inMetaDeclBody = true;
            }
                
            if (element instanceof LSFMetaCodeStatement && (enabled || ((LSFMetaCodeStatement) element).isInline())) {
                addUsageProcessing((LSFMetaCodeStatement) element);
            }

            if (element instanceof LSFModuleHeader) {
                inModuleHeader = true;
            }

            element = element.getParent();
        }

        if (!(inMetaBody || inMetaDeclBody)) {
            fireChangedNotMetaBody();
        }
        if (inModuleHeader) {
            fireChangedModuleHeader();
        }
    }

    private void fireAdded(PsiElement element) {
        Collection<PsiElement> children = findChildrenOfType(element, LSFMetaCodeStatement.class, LSFMetaCodeDeclarationStatement.class);
        for (PsiElement child : children) {
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
        if (!enabled)
            return;

        Collection<PsiElement> children = findChildrenOfType(element, LSFMetaCodeDeclarationStatement.class);
        for (PsiElement child : children) {
            if (child instanceof LSFMetaCodeDeclarationStatement) {
                // нужно перегенерить все usage'ы этого метакода
                addDeclProcessing((LSFMetaCodeDeclarationStatement) child);
            }
        }
    }
}
