package com.simpleplugin.psi;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ConcurrentHashMap;
import com.intellij.util.containers.ConcurrentHashSet;
import com.simpleplugin.LSFElementGenerator;
import com.simpleplugin.MetaCodeFragment;
import com.simpleplugin.meta.MetaTransaction;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.references.LSFMetaReference;
import com.simpleplugin.psi.stubs.types.indexes.ModuleIndex;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class MetaChangeDetector extends PsiTreeChangeAdapter implements ProjectComponent { //, EditorFactoryListener
    private static final Logger LOG = Logger.getInstance("#" + MetaChangeDetector.class.getName());
//    private final FileDocumentManager myDocumentManager;
    private final PsiManager myPsiManager;
//    private final FileEditorManager myFileEditorManager;
    private final Project myProject;
//    private final TemplateManager myTemplateManager;
    private final PsiDocumentManager myPsiDocumentManager;

    public MetaChangeDetector(
                                        final PsiDocumentManager psiDocumentManager,
//                                          final FileDocumentManager documentManager,
                                          final PsiManager psiManager,
//                                          final FileEditorManager fileEditorManager,
//                                          final TemplateManager templateManager,
                                          final Project project
    ) {
//        myDocumentManager = documentManager;
        myPsiDocumentManager = psiDocumentManager;
        myPsiManager = psiManager;
//        myFileEditorManager = fileEditorManager;
        myProject = project;
//        myTemplateManager = templateManager;
    }

    public static MetaChangeDetector getInstance(Project project){
        return project.getComponent(MetaChangeDetector.class);
    }

//    private final Map<VirtualFile, MyDocumentChangeAdapter> myListenerMap = new HashMap<VirtualFile, MyDocumentChangeAdapter>();

    private final TimerTask indexUpdater = new TimerTask() {
        @Override
        public void run() {
            if(enabled && !reprocessing && finishedReprocessing) {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
//                FileBasedIndex.getInstance().ensureUpToDate(StubUpdatingIndex.INDEX_ID, myProject, GlobalSearchScope.allScope(myProject));
                        if(!DumbService.isDumb(myProject))
                            ModuleIndex.getInstance().get("dumb", myProject, GlobalSearchScope.allScope(myProject));
                    }
                });
//                ApplicationManager.getApplication().runReadAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        FileBasedIndex.getInstance().ensureUpToDate(IdIndex.NAME, myProject, GlobalSearchScope.allScope(myProject));
//                    }
//                });
            }
        }
    };
    
    private Timer timer;
    private final static String ENABLED_META = "ENABLED_META";
    private final static String META_SYNC_MODE = "META_SYNC_MODE";
    
    @Override
    public void projectOpened() {
        myPsiManager.addPsiTreeChangeListener(this);
 /*       EditorFactory.getInstance().addEditorFactoryListener(this, myProject);
        Disposer.register(myProject, new Disposable() {
            @Override
            public void dispose() {
                myPsiManager.removePsiTreeChangeListener(MetaChangeDetector.this);
                LOG.assertTrue(myListenerMap.isEmpty(), myListenerMap);
            }
        });*/

        DumbService.getInstance(myProject).smartInvokeLater(
                new Runnable() {
                    public void run() {
                        setMetaSyncMode(PropertiesComponent.getInstance(myProject).getBoolean(META_SYNC_MODE, true));
                        setMetaEnabled(PropertiesComponent.getInstance(myProject).getBoolean(ENABLED_META, false), false);

                        timer = new Timer();
                        timer.schedule(indexUpdater, 10, 30);
                    }
                });
    }

    @Override
    public void projectClosed() {
        if(timer!=null) {
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
//        final PsiElement child = event.getChild();
//        if (child instanceof PsiFile) {
//            final PsiFile psiFile = (PsiFile)child;
//            final VirtualFile virtualFile = psiFile.getVirtualFile();
//            if (virtualFile != null && myListenerMap.containsKey(virtualFile)) {
//                final Document document = myDocumentManager.getDocument(virtualFile);
//                if (document != null) {
//                    removeDocListener(document, virtualFile);
//                } else {
//                    myListenerMap.remove(virtualFile);
//                }
//            }
//        }
//        System.out.print("CHILD REMOVED PARENT : " + event.getParent().getText() + " CHILD " + event.getChild().getText());
        fireChanged(event.getParent());
        fireRemoved(event.getChild());
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        fireChanged(event.getParent());
    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {
        fireChanged(event.getParent());
        fireRemoved(event.getOldChild());
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        fireChanged(event.getParent());
        fireAdded(event.getNewChild());
    }

    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent event) {
        fireChanged(event.getParent());
    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        fireChanged(event.getParent());
        fireAdded(event.getChild());
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
        System.out.print("CHILD MOVED FROM PARENT : " + event.getOldParent().getText() + " TO " + event.getNewParent().getText() + " CHILD " + event.getChild().getText());
    }

    private static <K> K pop(Set<K> set) {
        Iterator<K> iterator = set.iterator();
        if(iterator.hasNext()) { // тут synchronize'ить не надо, потому как удаление может быть только в этом потоке (и собственно в этом методе)
            K element = iterator.next();
            set.remove(element);
            return element;
        }
        return null;
    }
    private static int min(int a, int b) {
        return a>b?b:a;
    }
    private static String parseText(List<Pair<String, IElementType>> tokens, List<MetaTransaction.InToken> usages, List<String> decls) {
        return new MetaCodeFragment(decls, tokens).getCode(usages);
    }

    public static List<MetaTransaction.InToken> getNewTokens(List<Pair<String, IElementType>> tokens, List<MetaTransaction.InToken> usages, List<String> decls, List<List<MetaTransaction.ExtToken>> oldTokens) {
        return new MetaCodeFragment(decls, tokens).getNewTokens(usages, oldTokens);
    }

    private static int mapOffset(int offset, List<Pair<String, IElementType>> tokens, List<MetaTransaction.InToken> usages, List<String> decls) {
        return new MetaCodeFragment(decls, tokens).mapOffset(offset, usages);
    }
    
    public static PsiElement mapOffset(PsiElement element) {
        
        PsiElement current = element;
        LSFMetaCodeBody metaBody = null;
        LSFMetaReference metaUsage = null;
        int offset = 0;
        while(true) {
            if(current == null || current instanceof LSFFile)
                return null;
            
            if(current instanceof LSFMetaCodeBody) {
                metaBody = (LSFMetaCodeBody) current;
                break;
            }
            
            offset += current.getStartOffsetInParent();            
            current = current.getParent();
        }
        metaUsage = (LSFMetaReference) metaBody.getParent();
        
        if(!metaUsage.isCorrect())
            return null;
        
        LSFMetaDeclaration metaDecl = metaUsage.resolveDecl();
        if(metaDecl == null || !metaDecl.isCorrect())
            return null;

        String preceedingTab = metaUsage.getPreceedingTab();
        int actualOffset = offset;
        offset -= adjustMetaTabOffset(metaBody.getText().substring(0, offset), preceedingTab);

        for(PsiElement child : metaBody.getChildren()) {
            if(child instanceof LSFMetaCodeStatement) {
                if(child.getStartOffsetInParent() >= actualOffset - 1) // -1 потому как скобка
                    break;                    
                    
                LSFMetaCodeBody innerBody = ((LSFMetaCodeStatement) child).getMetaCodeBody();
                if(innerBody!=null) {
                    String text = innerBody.getText();
                    offset -= text.length() - adjustMetaTabOffset(text, preceedingTab);
                }
            }
        }

        return metaDecl.findOffsetInCode(mapOffset(offset, metaDecl.getMetaCode(), metaUsage.getUsageParams(), metaDecl.getDeclParams()));
    }

    private static int adjustMetaTabOffset(String text, String preceedingTab) {
        return (text.split("\n").length - 1) * preceedingTab.length() + 1;
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
        public final String whitespace;

        private ToParse(List<Pair<String, IElementType>> tokens, List<MetaTransaction.InToken> usages, List<String> decls, long version, String whitespace) {
            this.tokens = tokens;
            this.usages = usages;
            this.decls = decls;
            this.version = version;
            this.whitespace = whitespace;
        }

        public LSFMetaCodeBody parse(LSFFile file) {
            if(tokens == null)
                return null;
            else
                return LSFElementGenerator.createMetaBodyFromText(file, parseText(tokens, usages, decls), whitespace);
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
    public static void syncUsageProcessing(final LSFFile file, List<LSFMetaCodeStatement> usages) {
        for(final LSFMetaCodeStatement metaUsage : usages)
            if(metaUsage.isCorrect()) {
                final Result<ToParse> toParse = new Result<ToParse>();
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    public void run() {
                        if(metaUsage.isCorrect()) {
                            metaUsage.setAnotherFile(file);
                            LSFMetaDeclaration metaDecl = metaUsage.resolveDecl();
                            metaUsage.setAnotherFile(null);
                            
                            assert metaDecl == null || metaDecl.isValid();
                            if(metaDecl==null || !metaDecl.isCorrect())
                                toParse.setResult(new ToParse(null, null, null, version, null));
                            else
                                toParse.setResult(new ToParse(metaDecl.getMetaCode(), metaUsage.getUsageParams(), metaDecl.getDeclParams(), version, metaUsage.getPreceedingTab()));
                        }
                    }
                });
                
                if(toParse.getResult() != null) {
                    boolean old = ApplicationImpl.setExceptionalThreadWithReadAccessFlag(true);

                    metaUsage.setInlinedBody(toParse.getResult().parse(file));

                    ApplicationImpl.setExceptionalThreadWithReadAccessFlag(old);
                }
            }
    }
    
    private LinkedHashMap<Document, Long> changedDocs = new LinkedHashMap<Document, Long>(16, 0.75f, true);
    private int inlinePending = 0;
    private void inlinePend(LSFFile file, boolean sync) {
        synchronized (displaySync) {
            if(inlinePending==inlineProceeded) {
                if(!sync && !displayRunning) {
                    displayRunning = true;

                    final BackgroundableProcessIndicator indicator = new BackgroundableProcessIndicator(myProject, "Inlining metacode", PerformInBackgroundOption.ALWAYS_BACKGROUND, "cancel", "stop", false);
                    ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                        public void run() {
                        ProgressManager.getInstance().runProcess(new Runnable() {
                        public void run() {
                            assert displayRunning;

                            int idleTimes = 0;
                            while (true) {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                }
                                synchronized (displaySync) {
                                    if (inlinePending == inlineProceeded && idleTimes > 4) {
                                        displayRunning = false;
                                        return;
                                    } else {
                                        idleTimes++;
                                        indicator.setFraction(inlinePending == 0 ? 1.0d : (double) inlineProceeded / (double) inlinePending);
                                        indicator.setText((lastProceeded !=null ? "Last inlined : " + lastProceeded : "") + " " + inlineProceeded + "/" + inlinePending);
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
    private void inlineProceed(boolean easy, LSFFile file, boolean sync) {
        synchronized (displaySync) {
            if(easy)
                inlinePending--;
            else {
                inlineProceeded++;
                lastProceeded = file.getName();
            }

            if(inlinePending==inlineProceeded) {
                 inlinePending = 0;
                 inlineProceeded = 0;

                 finishedReprocessing = true;
            }
        }
    }
    
    private void inlinePostpone(final Runnable run, final boolean ui) {
        javax.swing.Timer timer = new javax.swing.Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(ui)
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
    private boolean actualize(GenParse gen, LSFFile file, boolean sync) {
        boolean result = actual(gen);
        if(!result)
            inlineProceed(true, file, sync);
        return result;
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
            final List<GenParse> genUsages = new ArrayList<GenParse>();
            final Iterator<LSFMetaCodeStatement> iterator = usages.iterator();
            while(iterator.hasNext()) {
                final LSFMetaCodeStatement metaUsage = iterator.next();
                final Result<ToParse> toParse = new Result<ToParse>();
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    public void run() {
                        version++; // синхронизация не волнует может быть и одна версия (если в рамках нескольких read'ов но не write'ов)
                        boolean keep = false;
                        if(metaUsage.isValid() && metaUsage.isCorrect()) {
                            if(!DumbService.isDumb(myProject)) {
                                LSFMetaDeclaration metaDecl = (forcedEnabled != null ? forcedEnabled : enabled) ? metaUsage.resolveDecl() : null;
                                assert metaDecl == null || metaDecl.isValid();
                                if(metaDecl==null || !declPending.processing.contains(getLongLivingDecl(metaDecl))) { // не обновляем, потому как все равно обновится при обработке metaDeclChanged
                                    if(metaDecl==null || !metaDecl.isCorrect())
                                        toParse.setResult(new ToParse(null, null, null, version, null));
                                    else
                                        toParse.setResult(new ToParse(metaDecl.getMetaCode(), metaUsage.getUsageParams(), metaDecl.getDeclParams(), version, metaUsage.getPreceedingTab()));
                                }
                            } else
                                keep = true;
                        }
                        if(!keep) {
                            metaUsage.setVersion(version);

                            boolean removed = usagesPending.processing.remove(metaUsage); // все равно плохо, изменение еще не "сохранено", а declPending уже обработаться и не найти этот usage
                            assert removed;
                            
                            iterator.remove();
                        }
                    }
                });

                if(toParse.getResult()!=null)
                    genUsages.add(new GenParse(metaUsage, toParse.getResult().parse(file), toParse.getResult().version));
            }
            final Document document = myPsiDocumentManager.getDocument(file);
            for(final GenParse gen : genUsages) {
                final Result<Runnable> runMetaText = new Result<Runnable>();
                runMetaText.setResult(new Runnable() {
                    public void run() {
                        if(!actualize(gen, file, sync)) // оптимизация
                            return;

                        myPsiDocumentManager.performForCommittedDocument(document, new Runnable() { // без perform for commited постоянно рассинхронизируется дерево с текстом
                            public void run() {
                                if(!actualize(gen, file, sync)) // оптимизация
                                    return;
                                
                                if(reprocessing) {
                                    inlinePostpone(runMetaText.getResult(), true);
                                    return;
                                }                                    
                        
                                if(!sync) { // в синхронном режиме не нужны задержки
                                    long current = System.currentTimeMillis();
        
                                    long lastTime = 0;
                                    if(!(changedDocs.size() < 2 || changedDocs.containsKey(document))) {
                                        Map.Entry<Document, Long> lastChanged = changedDocs.entrySet().iterator().next();
                                        lastTime = lastChanged.getValue();
                                        long timeElapsed = current - lastTime; 
                                        if(timeElapsed <= 200) {
            //                                System.out.println("postponed " + file.getName() + " " + lastTime + " " + current);
                                            inlinePostpone(runMetaText.getResult(), true);
                                            return;
                                        }
                                        changedDocs.remove(lastChanged.getKey());
                                    }
    
    //                                String docs = "";
    //                                for(Document d : changedDocs.keySet())
    //                                    docs += "," + myPsiDocumentManager.getPsiFile(d).getVirtualFile().getName();
    //                                System.out.println("proceeded " + file.getName() + " " + lastTime + " " + current + " " + docs);
                                    changedDocs.put(document, current);
                                }

                                inlineProceed(false, file, sync);
                  
                                CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                                    public void run() {
                                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                            public void run() {
                                                if (gen.usage.isValid() && gen.usage.isCorrect() && actual(gen)) { // can become not valid
//                                                    myPsiDocumentManager.commitDocument(document);
                
                                                    boolean prevEnabled = enabled;
                                                    enabled = false; // выключаем чтобы каскадно не вызывались события
                
                                                    gen.usage.setInlinedBody(gen.body);
                
                                                    enabled = prevEnabled;

//                                                    myPsiDocumentManager.commitDocument(document);
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
                        if(!actual(gen)) // оптимизация
                            return;

                        inlinePend(file, sync);
                        runMetaText.getResult().run();
                    }
                };

                ApplicationManager.getApplication().invokeLater(runGenUsage);
            }

            if(!usages.isEmpty())
                inlinePostpone(this, false); // еще раз запустим, так как некоторые в Dumb выполнялись
        }
    }

    private abstract class MetaPending<T, G> {
        public final ConcurrentHashSet<T> processing = new ConcurrentHashSet<T>();
        private Map<G, Set<T>> pending = new HashMap<G, Set<T>>();

        protected abstract G group(T element);
        protected abstract Runnable createAction(G group, Set<T> elements);

        private boolean flushing = false;
        private void flushAll() {
            for(Map.Entry<G, Set<T>> group : pending.entrySet()) {
//                System.out.println("flushed" + group.getValue().size() + " " + System.currentTimeMillis() + (group.getKey() instanceof LSFFile? ((LSFFile)group.getKey()).getName() : "" ));
                processing.addAll(group.getValue());
                ApplicationManager.getApplication().executeOnPooledThread(createAction(group.getKey(), group.getValue()));
            }
            pending = new HashMap<G, Set<T>>();
        }

        private void flushGroup(G group) {
            Set<T> flush = pending.remove(group);
//            System.out.println("flushed group" + flush.size() + " " + System.currentTimeMillis() + (group instanceof LSFFile? ((LSFFile)group).getName() : "" ));
            processing.addAll(flush);
            ApplicationManager.getApplication().executeOnPooledThread(createAction(group, flush));
        }

        private final Runnable flush = new Runnable() {
            public void run() {
/*                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }*/
                
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
            for(T statement : elements) {
                if(!processing.contains(statement) && extraCheck(statement)) {
                    synchronized (flush) {
                        G group = group(statement);
                        Set<T> pendEls = pending.get(group);
                        if(pendEls==null) {
                            pendEls = new HashSet<T>();
                            pending.put(group, pendEls);
                        }
                        pendEls.add(statement);
                        if(pendEls.size() > 4)
                            flushGroup(group);
                        if(pending.size() > 4)
                            flushAll();                            
                        if(!flushing) {
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
        assert enabled;
        usagesPending.add(used);
    }

    private void addForcedUsageProcessing(LSFFile file, List<LSFMetaCodeStatement> used, boolean sync, Boolean forcedEnabled) { // в синхронном режиме может вызываться должен быть достаточно быстрым
        usagesPending.processing.addAll(used);
        if(used.size() > 0)
            finishedReprocessing = false;
        new MetaUsageProcessing(file, new HashSet<LSFMetaCodeStatement>(used), sync, forcedEnabled).run();
    }

    private void addUsageProcessing(LSFMetaCodeStatement statement) { // в синхронном режиме может вызываться должен быть достаточно быстрым
        addUsageProcessing(Collections.singleton(statement));
    }

    private ConcurrentHashMap<LongLivingMeta, List<LSFMetaCodeStatement>> cacheUsages = new ConcurrentHashMap<LongLivingMeta, List<LSFMetaCodeStatement>>();
    
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
            if(!DumbService.isDumb(myProject)) {
                for(final LongLivingMeta metaDecl : decls) {
                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        public void run() {                        
                            List<LSFMetaCodeStatement> usages = null;
                            if(metaDecl.file.isValid()) {
                                usages = cacheUsages.get(metaDecl);
                                if(usages==null) {
                                    usages = LSFResolver.findMetaUsages(metaDecl.name, metaDecl.paramCount, metaDecl.file);
                                    cacheUsages.put(metaDecl, usages);
                                }
                            }
                            boolean removed = declPending.processing.remove(metaDecl);
                            assert removed;
                                
                            if(usages!=null)
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

    private void addDeclProcessing(LSFMetaDeclaration decl) {
        assert ApplicationManager.getApplication().isWriteAccessAllowed();

        declPending.add(getLongLivingDecl(decl));
    }

    private boolean reprocessing = false;
    private boolean finishedReprocessing = true;

    private boolean enabled = false;
    
    public void toggleMetaEnabled() {
        setMetaEnabled(!enabled, true);
    }

    public void setMetaEnabled(boolean enabled, boolean reprocess) {
        this.enabled = enabled;
        PropertiesComponent.getInstance(myProject).setValue(ENABLED_META, Boolean.toString(enabled));

/*        final ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance(myProject);
        if(enabled)
            vcsManager.startBackgroundVcsOperation();
        else {
            if(this.enabled) {
                ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                    public void run() {
                        vcsManager.stopBackgroundVcsOperation();                
                    }
                });
            }
        }*/

        if(reprocess)
            reprocessAllDocuments(syncMode);
    }

    public boolean getMetaEnabled() {
        return enabled;
    }    

    public void toggleMetaSyncMode() {
        setMetaSyncMode(!syncMode);
    } 
    
    private boolean syncMode;
    public void setMetaSyncMode(boolean syncMode) {
        this.syncMode = syncMode;
        PropertiesComponent.getInstance(myProject).setValue(META_SYNC_MODE, Boolean.toString(syncMode));        
    }
    
    public boolean getMetaSyncMode() {
        return syncMode;
    }
    
    public void reprocessFile(LSFFile file, boolean enabled) {
        addForcedUsageProcessing(file, file.getMetaCodeStatementList(), getMetaSyncMode(), enabled);
    }

    public void reprocessAllDocuments() {
        reprocessAllDocuments(getMetaSyncMode());
    }        

    public void reprocessAllDocuments(final boolean sync) {
        final Progressive run = new Progressive() {
            public void run(final @NotNull ProgressIndicator indicator) {
                reprocessing = true;

                final Collection<String> allKeys = ApplicationManager.getApplication().runReadAction(new Computable<Collection<String>>() {
                    public Collection<String> compute() {
                        return ModuleIndex.getInstance().getAllKeys(myProject);
                    }
                });
                
                double coeff = sync ? 0.5d : 1.0d;

                int i = 0;
                for (final String module : allKeys) {
                    indicator.setText("Processing : " + module);
                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        @Override
                        public void run() {
                            Collection<LSFModuleDeclaration> moduleDeclarations = ModuleIndex.getInstance().get(module, myProject, GlobalSearchScope.allScope(myProject));
                            for (LSFModuleDeclaration declaration : moduleDeclarations) {
                                LSFFile file = declaration.getLSFFile();
                                List<LSFMetaCodeStatement> metaStatements = file.getMetaCodeStatementList();
                                indicator.setText2("Statements : " + metaStatements.size());
                                addForcedUsageProcessing(file, metaStatements, sync, null);
                                indicator.setText2("");
                            }
                        }
                    });
                    indicator.setFraction(((double) i++) * coeff / allKeys.size());
                }

                reprocessing = false;

                if (sync) {
                    while (!finishedReprocessing) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }

                        indicator.setFraction((inlinePending == 0 ? 1.0d : (double) inlineProceeded / (double) inlinePending)*coeff + coeff);
                        indicator.setText((lastProceeded !=null ? "Last inlined : " + lastProceeded : "") + " " + inlineProceeded + "/" + inlinePending);
                    }
                }
            }
        };

        Task task;
        if(sync) {
            task = new Task.Modal(myProject, "Updating metacode", true) {
                public void run(final @NotNull ProgressIndicator indicator) {
                    run.run(indicator);
                }
            };
        } else {
            task = new Task.Backgroundable(myProject, "Marking metacode") {
                public void run(final @NotNull ProgressIndicator indicator) {
                    run.run(indicator);
                }
            };
        }
        ProgressManager.getInstance().run(task);
    }

    private void fireChanged(PsiElement element) {
        if(!enabled)
            return;

        boolean inMetaBody = false;
        boolean inModuleHeader = false;
        while(element != null && !(element instanceof LSFFile)) {
            if(element instanceof LSFMetaCodeDeclarationStatement)
                addDeclProcessing((LSFMetaCodeDeclarationStatement)element);
            
            if(element instanceof LSFAnyTokens || element instanceof LSFMetaCodeBody) {
                inMetaBody = true;
            }

            if(element instanceof LSFMetaCodeStatement)
                addUsageProcessing((LSFMetaCodeStatement)element);
            
            if(element instanceof LSFModuleHeader)
                inModuleHeader = true; 

            element = element.getParent();
        }

        if(!inMetaBody)
            fireChangedNotMetaBody();
        if(inModuleHeader)
            fireChangedModuleHeader();
    }

    private void fireAdded(PsiElement element) {
        if(!enabled) {
/*            assert !(element instanceof LSFStatements);
            if(element instanceof LSFMetaCodeBody) // нужно перегенерировать тело использования
                for(PsiElement child : ((LSFMetaCodeBody)element).getStatements().getChildren())
                    if(child instanceof LSFMetaCodeStatement) // нужно перегенерировать тело использования
                        addUsageProcessing((LSFMetaCodeStatement)child);
*/
            return;
        }

        if(element instanceof LSFMetaCodeStatement) // нужно перегенерировать тело использования
            addUsageProcessing((LSFMetaCodeStatement)element);

        if(element instanceof LSFMetaCodeDeclarationStatement) // нужно перегенерить все usage'ы этого метакода
            addDeclProcessing((LSFMetaCodeDeclarationStatement)element);

        if(element instanceof LSFFile) { // перегенерировать все внутренние использования
            for(PsiElement child : element.getChildren()) {
                if(child instanceof LSFMetaCodeStatement)
                    addUsageProcessing((LSFMetaCodeStatement)child);
                if(child instanceof LSFMetaCodeDeclarationStatement)
                    addDeclProcessing((LSFMetaCodeDeclarationStatement)child);
            }
        }
    }

    private void fireRemoved(PsiElement element) {
        if(!enabled)
            return;

        if(element instanceof LSFMetaCodeDeclarationStatement) // нужно перегенерить все usage'ы этого метакода
            addDeclProcessing((LSFMetaCodeDeclarationStatement)element);

        if(element instanceof LSFFile) { // перегенерировать все внутренние использования
            for(PsiElement child : element.getChildren()) {
                if(child instanceof LSFMetaCodeDeclarationStatement)
                    addDeclProcessing((LSFMetaCodeDeclarationStatement)child);
            }
        }
    }

/*    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        final Editor editor = event.getEditor();
        if (editor.getProject() != myProject) return;
        addDocListener(editor.getDocument());
    }

    public void addDocListener(Document document) {
        if (document == null) return;
        final VirtualFile file = myDocumentManager.getFile(document);
        if (file != null && file.isValid() && !myListenerMap.containsKey(file)) {
            final PsiFile psiFile = myPsiManager.findFile(file);
            if (psiFile == null || !psiFile.isPhysical()) return;
            final MyDocumentChangeAdapter adapter = new MyDocumentChangeAdapter();
            document.addDocumentListener(adapter);
            myListenerMap.put(file, adapter);
        }
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        final EditorEx editor = (EditorEx)event.getEditor();
        final Document document = editor.getDocument();

        VirtualFile file = myDocumentManager.getFile(document);
        if (file == null) {
            file = editor.getVirtualFile();
        }
        if (file != null && file.isValid()) {
            if (myFileEditorManager.isFileOpen(file)) {
                return;
            }
        }
        removeDocListener(document, file);
    }

    public void removeDocListener(Document document, VirtualFile file) {
        final MyDocumentChangeAdapter adapter = myListenerMap.remove(file);
        if (adapter != null) {
            document.removeDocumentListener(adapter);
        }
    }*/

/*    private class MyDocumentChangeAdapter extends DocumentAdapter {
        private final @NonNls
        String PASTE_COMMAND_NAME = EditorBundle.message("paste.command.name");
        private final @NonNls String TYPING_COMMAND_NAME = EditorBundle.message("typing.in.editor.command.name");

        public MyDocumentChangeAdapter() {
        }

        @Override
        public void beforeDocumentChange(DocumentEvent e) {
            if (DumbService.isDumb(myProject)) return;

            final Document document = e.getDocument();
            final PsiDocumentManager documentManager = myPsiDocumentManager;

            if (!documentManager.isUncommited(document)) {
                final PsiFile file = documentManager.getPsiFile(document);
                if (file != null) {
                    final PsiElement element = file.findElementAt(e.getOffset());
                    if (element != null) {
                        System.out.println("DOCUMENT CHANGED : element " + element.getText() + " offs " + e.getOffset() + " oldText " + e.getOldFragment() + " newText " + e.getNewFragment());
                    } else
                        System.out.println("SOMEWHERE : " + " offs " + e.getOffset() + " oldText " + e.getOldFragment() + " newText " + e.getNewFragment());
                }
            }
        }
    }*/
}
