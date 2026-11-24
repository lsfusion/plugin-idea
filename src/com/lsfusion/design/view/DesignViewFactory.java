package com.lsfusion.design.view;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.ui.content.impl.ContentImpl;
import org.jetbrains.annotations.NotNull;

// One per project: the Design tool window and the PSI events that update it belong to a project, and an
// application-wide instance let the project that opened the window last take over the other projects' events.
@Service(Service.Level.PROJECT)
public final class DesignViewFactory {
    public static DesignViewFactory getInstance(@NotNull Project project) {
        return project.getService(DesignViewFactory.class);
    }

    private DesignView designView;
    private ToolWindowEx toolWindow;

    public void initToolWindow(Project project, ToolWindowEx toolWindow) {
        this.toolWindow = toolWindow;

        DesignView view = new DesignView(project, toolWindow);
        designView = view;

        ContentImpl content = new ContentImpl(view, "", true);
        // Dispose the design tabs together with the tool window content (project close), and drop the
        // references to them, so that a disposed view gets no more events.
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
