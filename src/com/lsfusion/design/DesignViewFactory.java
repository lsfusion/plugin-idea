package com.lsfusion.design;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.impl.DataManagerImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormStatementImpl;

public class DesignViewFactory {
    private static final DesignViewFactory INSTANCE = new DesignViewFactory();

    public static DesignViewFactory getInstance() {
        return INSTANCE;
    }

    private DesignView designView;
    private ToolWindowEx toolWindow;

    public void initToolWindow(Project project, ToolWindowEx toolWindow) {
        this.toolWindow = toolWindow;

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        LSFFormDeclaration formDeclaration = null;
        LSFModuleDeclaration moduleDeclaration = null;

        if (editor != null) {
            DataContext dataContext = new DataManagerImpl.MyDataContext(editor.getComponent());
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
                    moduleDeclaration = ((LSFFile) targetElement.getContainingFile()).getModuleDeclaration();
                }
            }
        }

        if (moduleDeclaration != null && formDeclaration != null) {
            designView = new DesignView(project, moduleDeclaration, formDeclaration, toolWindow);

            toolWindow.getComponent().removeAll();
            toolWindow.getComponent().add(designView);
            toolWindow.getComponent().repaint();
        }
    }

    public void updateView(LSFModuleDeclaration module, LSFFormDeclaration formDeclaration) {
        if (toolWindow != null && toolWindow.isVisible()) {
            designView.scheduleRebuild(module, formDeclaration);
        }
    }
}
