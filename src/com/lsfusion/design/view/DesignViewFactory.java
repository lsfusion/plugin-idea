package com.lsfusion.design.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
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

        DesignView view = new DesignView(project, toolWindow);
        designView = view;

        ContentImpl content = new ContentImpl(view, "", true);
        // Dispose the design tabs together with the tool window content (project close), and drop the
        // application-level singleton references so the closed project's view is neither leaked nor
        // reachable from another project's PSI events.
        content.setDisposer(() -> {
            Disposer.dispose(view);
            if (designView == view) {
                designView = null;
                this.toolWindow = null;
            }
        });
        toolWindow.getContentManager().addContent(content);

        view.toolWindowInitialized();

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
