package com.lsfusion.design;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormStatementImpl;
import org.jetbrains.annotations.NotNull;

public class FormDesignChangeDetector extends PsiTreeChangeAdapter implements ProjectComponent {
    private PsiDocumentManager psiDocumentManager;
    private final PsiManager psiManager;
    private Project project = null;

    public FormDesignChangeDetector(final PsiDocumentManager psiDocumentManager, final PsiManager psiManager, final Project project) {
        this.psiDocumentManager = psiDocumentManager;
        this.psiManager = psiManager;
        this.project = project;
    }

    @Override
    public void projectOpened() {
        psiManager.addPsiTreeChangeListener(this);
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "FormDesignChangeDetector";
    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event.getChild(), event.getFile());
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event.getChild(), event.getFile());
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event.getChild(), event.getFile());
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event.getChild(), event.getFile());
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event.getChild(), event.getFile());
    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event.getChild(), event.getFile());
    }
    
    private boolean alreadyPending; // не синхронизируем, так как все в edt

    private void fireChildChanged(PsiElement element, PsiFile file) {
        if (element == null || file == null || !DesignViewFactory.getInstance().windowIsVisible()) {
            return;
        }

        final Document document = psiDocumentManager.getDocument(file);
        if (document == null || DumbService.isDumb(project)) {
            return;
        }
        
        if(!alreadyPending) {
            alreadyPending = true;
            DumbService.getInstance(project).smartInvokeLater(new Runnable() { // так как это событие вызывается до commitTransaction, modificationStamp'ы и unsavedDocument'ы не обновились, а значит к индексам обращаться нельзя
                @Override
                public void run() {
                    try {
                        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                        LSFFormDeclaration formDeclaration = null;
                        LSFModuleDeclaration module = null;

                        if (editor != null) {
                            DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
                            PsiElement targetElement = ConfigurationContext.getFromContext(dataContext).getPsiLocation();

                            if (targetElement != null) {
                                LSFFormExtend formExtend = PsiTreeUtil.getParentOfType(targetElement, LSFFormExtend.class);
                                if (formExtend != null) {
                                    formDeclaration = ((LSFFormStatementImpl) formExtend).resolveFormDecl();
                                } else {
                                    LSFDesignStatement designStatement = PsiTreeUtil.getParentOfType(targetElement, LSFDesignStatement.class);
                                    if (designStatement != null) {
                                        formDeclaration = designStatement.resolveFormDecl();
                                    }
                                }

                                if (targetElement.getContainingFile() instanceof LSFFile) {
                                    module = ((LSFFile) targetElement.getContainingFile()).getModuleDeclaration();
                                }
                            }
                        }

                        if (module != null && formDeclaration != null) {
                            DesignViewFactory.getInstance().updateView(module, formDeclaration);
                        }
                    } catch (PsiInvalidElementAccessException ignored) {
                    }
                    
                    alreadyPending = false;
                }
            });
        }
    }
}
