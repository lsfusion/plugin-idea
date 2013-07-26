package com.simpleplugin.psi;

import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorBundle;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.containers.ConcurrentHashSet;
import com.intellij.util.containers.HashMap;
import com.simpleplugin.LSFElementGenerator;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetaChangeDetector extends PsiTreeChangeAdapter implements ProjectComponent { //, EditorFactoryListener
    private static final Logger LOG = Logger.getInstance("#" + MetaChangeDetector.class.getName());
//    private final FileDocumentManager myDocumentManager;
    private final PsiManager myPsiManager;
//    private final FileEditorManager myFileEditorManager;
    private final Project myProject;
//    private final TemplateManager myTemplateManager;
//    private final PsiDocumentManager myPsiDocumentManager;

    public MetaChangeDetector(
//                                        final PsiDocumentManager psiDocumentManager,
//                                          final FileDocumentManager documentManager,
                                          final PsiManager psiManager,
//                                          final FileEditorManager fileEditorManager,
//                                          final TemplateManager templateManager,
                                          final Project project
    ) {
//        myDocumentManager = documentManager;
//        myPsiDocumentManager = psiDocumentManager;
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
    }

    @Override
    public void projectClosed() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ChangeSignatureGestureDetector";
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }
/*
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
        fireRemoved(event.getChild());
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        fireChanged(event.getParent());
    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {
        fireRemoved(event.getOldChild());
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        fireChanged(event.getParent());
        fireAdded(event.getNewChild());
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
    private ConcurrentHashSet<LSFMetaCodeStatement> metaUsagesChanged = new ConcurrentHashSet<LSFMetaCodeStatement>(); // только after (before не интересует)
    private ConcurrentHashSet<String> metaDeclChanged = new ConcurrentHashSet<String>(); // в before и after

    private boolean metaExecuting;
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
    private boolean generating = true;
    private final Runnable metaProcessing = new Runnable() {
        public void run() {
            Runnable run = null;

            final LSFMetaCodeStatement metaUsage = pop(metaUsagesChanged);
            if(metaUsage != null) {
                run = new Runnable() {
                    public void run() {
                        if(metaUsage.isValid()) {
                            LSFMetaIdUsage metaIdUsage = metaUsage.getMetaIdUsage();
                            if(!metaDeclChanged.contains(metaIdUsage.getText())) { // не обновляем, потому как все равно обновится при обработке metaDeclChanged
                                PsiElement resolve = metaIdUsage.resolve();
                                if(resolve != null)
                                    resolve = resolve.getParent();
                                LSFMetaCodeDeclarationStatement metaCode = (LSFMetaCodeDeclarationStatement) resolve;
                                final String fMetaText;
                                if(metaCode==null) {
                                    fMetaText = null;
                                } else {
                                    LSFMetaCodeBody metaCodeBody = metaCode.getMetaCodeBody();
                                    String metaText = metaCodeBody.getText();
                                    List<String> usages = LSFResolver.getIDs(metaUsage.getIdList());
                                    List<String> decls = LSFResolver.getIDs(metaCode.getIdList());
                                    for(int i=0,size=min(usages.size(), decls.size());i<size;i++)
                                        metaText = metaText.replace("##" + decls.get(i), usages.get(i));
                                    fMetaText = metaText;
                                }
                                ApplicationManager.getApplication().invokeLater(new Runnable() {
                                    public void run() {
                                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                            public void run() {
                                                if(metaUsage.isValid()) { // мог уже стать не valid
                                                    generating = true;

                                                    LSFMetaCodeBody body = metaUsage.getMetaCodeBody();
                                                    if(fMetaText==null) {
                                                        if(body!=null)
                                                            body.delete();
                                                    } else {
                                                        LSFMetaCodeBody genBody = LSFElementGenerator.createMetaBodyFromText(myProject, fMetaText);
                                                        if(body != null) {
                                                            body.replace(genBody);
                                                        } else {
                                                            metaUsage.getNode().addChild(genBody.getNode(), metaUsage.getIdList().getNode().getTreeNext().getTreeNext()); // идем до закрывающей скобки и точки с запятой
                                                        }
                                                    }

                                                    generating = true;
                                                }

                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                };
            } else {
                final String metaDecl = pop(metaDeclChanged);
                if(metaDecl != null) {
                    run = new Runnable() {
                        public void run() {
                            metaUsagesChanged.addAll(LSFResolver.findMetaUsages(myProject, metaDecl));
                        }
                    };
                }
            }

            if(run!=null)
                ApplicationManager.getApplication().runReadAction(run);

            if(metaUsagesChanged.isEmpty() && metaDeclChanged.isEmpty())
                metaExecuting = false;
            else
                ApplicationManager.getApplication().executeOnPooledThread(metaProcessing);
        }
    };

    private void fireChanged(PsiElement element) {
        if(generating)
            return;

        boolean metaChanged = false; // оптимизация
        while(!(element instanceof PsiFile)) {
            if(element instanceof LSFMetaCodeDeclarationStatement) {
                metaDeclChanged.add(((LSFMetaCodeDeclarationStatement)element).getSimpleNameWithCaption().getCompoundID().getText()); //
                metaChanged = true;
            }

            if(element instanceof LSFMetaCodeStatement) {
                metaUsagesChanged.add((LSFMetaCodeStatement) element);
                metaChanged = true;
            }

            element = element.getParent();
        }

        if(metaChanged)
            ensureMetaIsExecuting();
    }

    private void ensureMetaIsExecuting() {
        if(!metaExecuting) {
            metaExecuting = true;
            ApplicationManager.getApplication().executeOnPooledThread(metaProcessing);
        }
    }

    private void fireAdded(PsiElement element) {
        if(generating)
            return;

        boolean metaChanged = false;

        if(element instanceof LSFMetaCodeStatement) { // нужно перегенерировать тело использования
            metaUsagesChanged.add((LSFMetaCodeStatement) element);
            metaChanged = true;
        }

        if(element instanceof LSFMetaCodeDeclarationStatement) { // нужно перегенерить все usage'ы этого метакода
            LSFMetaCodeDeclarationStatement metaDeclare = (LSFMetaCodeDeclarationStatement) element;
            metaDeclChanged.add(metaDeclare.getSimpleNameWithCaption().getCompoundID().getText());
            metaChanged = true;
        }
        if(element instanceof PsiFile || element instanceof LSFMetaCodeDeclarationStatement) { // перегенерировать все внутренние использования
            for(PsiElement child : (element instanceof LSFMetaCodeDeclarationStatement?((LSFMetaCodeDeclarationStatement)element).getMetaCodeBody():element).getChildren())
                if(child instanceof LSFMetaCodeStatement) {
                    metaUsagesChanged.add((LSFMetaCodeStatement) child);
                    metaChanged = true;
                }
        }

        if(metaChanged)
            ensureMetaIsExecuting();
    }

    private void fireRemoved(PsiElement element) {
        if(generating)
            return;

        if(element instanceof LSFMetaCodeDeclarationStatement) { // нужно перегенерить все usage'ы этого метакода
            LSFMetaCodeDeclarationStatement metaDeclare = (LSFMetaCodeDeclarationStatement) element;
            metaDeclChanged.add(metaDeclare.getSimpleNameWithCaption().getCompoundID().getText());
        }
    }
  */
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
