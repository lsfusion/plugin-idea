package com.lsfusion.design.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.ui.content.impl.ContentImpl;

public class DesignViewFactory {
    private static final DesignViewFactory INSTANCE = new DesignViewFactory();

    public static DesignViewFactory getInstance() {
        return INSTANCE;
    }

    private DesignView designView;
    private ToolWindowEx toolWindow;

    public void initToolWindow(Project project, ToolWindowEx toolWindow) {
        this.toolWindow = toolWindow;

        designView = new DesignView(project, toolWindow);

        ContentImpl content = new ContentImpl(designView, "", true);
        toolWindow.getContentManager().addContent(content);
        
        designView.toolWindowInitialized();

        DesignView.openFormUnderCaretDesign(project, this::updateView);
    }

    public boolean windowIsVisible() {
        return toolWindow != null && toolWindow.isVisible();
    }

    public void updateView(DesignView.TargetForm targetForm) {
        if (windowIsVisible()) {
            designView.updateView(targetForm);
        }
    }

    public void designCodeChanged(PsiTreeChangeEvent event) {
        if (designView != null) {
            designView.designCodeChanged(event);
        }
    }
}
