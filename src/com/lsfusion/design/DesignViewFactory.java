package com.lsfusion.design;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.content.impl.ContentImpl;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFLocalSearchScope;
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
        LSFLocalSearchScope localScope = null;

        if (editor != null) {
            DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
            PsiElement targetElement = ConfigurationContext.getFromContext(dataContext).getPsiLocation();

            if (targetElement != null) {
                LSFFormExtend formExtend = PsiTreeUtil.getParentOfType(targetElement, LSFFormExtend.class);
                if (formExtend != null) {
                    localScope = LSFLocalSearchScope.createFrom(formExtend);
                    formDeclaration = ((LSFFormStatementImpl) formExtend).resolveFormDecl();
                } else {
                    LSFDesignStatement designStatement = PsiTreeUtil.getParentOfType(targetElement, LSFDesignStatement.class);
                    if (designStatement != null) {
                        localScope = LSFLocalSearchScope.createFrom(designStatement);
                        formDeclaration = designStatement.resolveFormDecl();
                    }
                }

                if (targetElement.getContainingFile() instanceof LSFFile) {
                    moduleDeclaration = ((LSFFile) targetElement.getContainingFile()).getModuleDeclaration();
                }
            }
        }

        designView = new DesignView(project, toolWindow);

        ContentImpl content = new ContentImpl(designView, "", true);
        toolWindow.getContentManager().addContent(content);

        if (moduleDeclaration != null && formDeclaration != null) {
            designView.scheduleRebuild(moduleDeclaration, formDeclaration, localScope);
        }
    }
    
    public boolean windowIsVisible() {
        return toolWindow != null && toolWindow.isVisible();
    }

    public void updateView(LSFModuleDeclaration module, LSFFormDeclaration formDeclaration, LSFLocalSearchScope localScope) {
        if (windowIsVisible()) {
            designView.scheduleRebuild(module, formDeclaration, localScope);
        }
    }
}
