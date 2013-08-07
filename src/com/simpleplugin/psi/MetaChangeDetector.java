package com.simpleplugin.psi;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ConcurrentHashSet;
import com.simpleplugin.LSFElementGenerator;
import com.simpleplugin.MetaCodeFragment;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;
import com.simpleplugin.psi.stubs.types.indexes.ModuleIndex;
import org.jetbrains.annotations.NotNull;

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
                        reprocessAllDocuments();
                    }
                });
    }

    @Override
    public void projectClosed() {
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
    private static String parseText(List<String> tokens, List<String> usages, List<String> decls) {
        return new MetaCodeFragment(decls, tokens).getCode(usages);
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
        return new LongLivingMeta(decl.getGlobalName(), decl.getParamCount(), decl.getLSFFile());
    }

    private static class ToParse {

        private final List<String> tokens;
        private final List<String> usages;
        private final List<String> decls;
        public final long version;
        public final String whitespace;

        private ToParse(List<String> tokens, List<String> usages, List<String> decls, long version, String whitespace) {
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

    private static class Result<T> {
        T result;

        private T getResult() {
            return result;
        }

        private void setResult(T result) {
            this.result = result;
        }
    }

    private static long version;

    private static String getFInlineName(PsiElement element, Result<Integer> cresult) {
        String result = "";
        int count = 1;
        while (!(element instanceof LSFFile)) {
            if(element instanceof LSFMetaCodeStatement) {
                result = ((LSFMetaCodeStatement)element).getSimpleName().getText() + "(" + ((LSFMetaCodeStatement)element).getMetaCodeIdList().getText() + ")." + result;
                count++;
            }
            element = element.getParent();
        }
        result = ((LSFFile)element).getName() + "." + result + " " + count;
        cresult.setResult(count);
        return result;
    }

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
    
    public class MetaUsageProcessing implements Runnable {
        private LSFFile file;
        private Set<LSFMetaCodeStatement> usages;

        private MetaUsageProcessing(LSFFile file, Set<LSFMetaCodeStatement> usages) {
            this.file = file;
            this.usages = usages;
        }

        public void run() {
            final List<GenParse> genUsages = new ArrayList<GenParse>();
            for(final LSFMetaCodeStatement metaUsage : usages) {
                final Result<ToParse> toParse = new Result<ToParse>();
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    public void run() {
                        version++; // синхронизация не волнует может быть и одна версия (если в рамках нескольких read'ов но не write'ов)
                        if(metaUsage.isValid() && metaUsage.isCorrect()) {
                            LSFMetaDeclaration metaDecl = metaUsage.resolveDecl();
                            assert metaDecl == null || metaDecl.isValid();
                            if(metaDecl==null || !declPending.processing.contains(getLongLivingDecl(metaDecl))) { // не обновляем, потому как все равно обновится при обработке metaDeclChanged
                                if(metaDecl==null || !metaDecl.isCorrect())
                                    toParse.setResult(new ToParse(null, null, null, version, null));
                                else
                                    toParse.setResult(new ToParse(metaDecl.getMetaCode(), metaUsage.getUsageParams(), metaDecl.getDeclParams(), version, metaUsage.getPreceedingTab()));
                            }
                        }
                        boolean removed = usagesPending.processing.remove(metaUsage); // все равно плохо, изменение еще не "сохранено", а declPending уже обработаться и не найти этот usage
                        assert removed;
                    }
                });

                if(toParse.getResult()!=null)
                    genUsages.add(new GenParse(metaUsage, toParse.getResult().parse(file), toParse.getResult().version));
            }
            final Document document = myPsiDocumentManager.getDocument(file);
            for(final GenParse gen : genUsages) {
                final Runnable runMetaText = new Runnable() {
                    public void run() {
                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                            @Override
                            public void run() {
                            if (gen.usage.isValid() && gen.usage.getVersion() < gen.version) { // can become not valid
                                myPsiDocumentManager.commitDocument(document);
                                        
                                generating = true;
    
                                gen.usage.setInlinedBody(gen.body);
    
                                generating = false;
    
                                gen.usage.setVersion(gen.version);
                            }
                            }
                        });
                    }
                };

                ApplicationManager.getApplication().invokeLater(runMetaText);
            }
/*            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() { // без perform for commited постоянно рассинхронизируется дерево с текстом
                    myPsiDocumentManager.performForCommittedDocument(myPsiDocumentManager.getDocument(file), runMetaText); 
                }
            });*/
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

        public void add(Collection<T> elements) {
            for(T statement : elements) {
                if(!processing.contains(statement)) {
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

        protected LSFFile group(LSFMetaCodeStatement element) {
            return element.getLSFFile();
        }

        protected Runnable createAction(LSFFile file, Set<LSFMetaCodeStatement> elements) {
            return new MetaUsageProcessing(file, elements);
        }
    }
    private final MetaUsagesPending usagesPending = new MetaUsagesPending();

    private void addUsageProcessing(Collection<LSFMetaCodeStatement> used) { // в синхронном режиме может вызываться должен быть достаточно быстрым
        usagesPending.add(used);
    }

    private void addForcedUsageProcessing(LSFFile file, List<LSFMetaCodeStatement> used) { // в синхронном режиме может вызываться должен быть достаточно быстрым
        usagesPending.processing.addAll(used);
        new MetaUsageProcessing(file, new HashSet<LSFMetaCodeStatement>(used)).run();
    }

    private void addUsageProcessing(LSFMetaCodeStatement statement) { // в синхронном режиме может вызываться должен быть достаточно быстрым
/*        Result<Integer> cresult = new Result<Integer>();
        String name = getFInlineName(statement, cresult);
        if(proceeded.contains(name))
            statement = statement;
        System.out.println(name);*/
        addUsageProcessing(Collections.singleton(statement));
    }

    private class MetaDeclProcessing implements Runnable {
        private Set<LongLivingMeta> decls;

        private MetaDeclProcessing(Set<LongLivingMeta> decls) {
            this.decls = decls;
        }

        public void run() {
            for(final LongLivingMeta metaDecl : decls) {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    public void run() {                        
                        List<LSFMetaCodeStatement> usages = null;
                        if(metaDecl.file.isValid()) {
                            usages = LSFResolver.findMetaUsages(metaDecl.name, metaDecl.paramCount, metaDecl.file);
                        boolean removed = declPending.processing.remove(metaDecl);
                        assert removed;
                            
                        if(usages!=null)
                            addUsageProcessing(usages);
                    }
                }});
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

    private void addDeclProcessing(LSFMetaDeclaration decl) {
        assert ApplicationManager.getApplication().isWriteAccessAllowed();

        declPending.add(getLongLivingDecl(decl));
    }

    private boolean generating = false;

    public void reprocessAllDocuments() {
        final Collection<String> allKeys = ApplicationManager.getApplication().runReadAction(new Computable<Collection<String>>() {
            public Collection<String> compute() {
                return ModuleIndex.getInstance().getAllKeys(myProject);
            }
        });
        ProgressManager.getInstance().run(new Task.Backgroundable(myProject, "Inlining metacode") {
            public void run(@NotNull ProgressIndicator indicator) {
                int i=0;
                for(final String module : allKeys) {
                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        @Override
                        public void run() {
                            Collection<LSFModuleDeclaration> moduleDeclarations = ModuleIndex.getInstance().get(module, myProject, GlobalSearchScope.allScope(myProject));
                            for(LSFModuleDeclaration declaration : moduleDeclarations) {
                                LSFFile file = declaration.getLSFFile();
                                addForcedUsageProcessing(file, file.getStatements().getMetaCodeStatementList());
                            }
                        }
                    });
                    indicator.setFraction(((double)i++)/allKeys.size());
                }
            }
        });
    }

    private void fireChanged(PsiElement element) {
        if(generating)
            return;

        while(element != null && !(element instanceof LSFFile)) {
            if(element instanceof LSFMetaCodeDeclarationStatement)
                addDeclProcessing((LSFMetaCodeDeclarationStatement)element);

            if(element instanceof LSFMetaCodeStatement)
                addUsageProcessing((LSFMetaCodeStatement)element);

            element = element.getParent();
        }
    }

    private void fireAdded(PsiElement element) {
        if(generating) {
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

        if(element instanceof LSFFile)
            element = ((LSFFile)element).getStatements();

        if(element instanceof LSFStatements) { // перегенерировать все внутренние использования
            for(PsiElement child : ((LSFStatements)element).getChildren()) {
                if(child instanceof LSFMetaCodeStatement)
                    addUsageProcessing((LSFMetaCodeStatement)child);
                if(child instanceof LSFMetaCodeDeclarationStatement)
                    addDeclProcessing((LSFMetaCodeDeclarationStatement)child);
            }
        }
    }

    private void fireRemoved(PsiElement element) {
        if(generating)
            return;

        if(element instanceof LSFMetaCodeDeclarationStatement) // нужно перегенерить все usage'ы этого метакода
            addDeclProcessing((LSFMetaCodeDeclarationStatement)element);

        if(element instanceof LSFFile)
            element = ((LSFFile)element).getStatements();

        if(element instanceof LSFStatements) { // перегенерировать все внутренние использования
            for(PsiElement child : ((LSFStatements)element).getChildren()) {
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
