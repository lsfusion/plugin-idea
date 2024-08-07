package com.lsfusion.dependencies;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import org.jetbrains.annotations.NotNull;

public class DependenciesViewToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DependenciesViewFactory factory = DependenciesViewFactory.getInstance();
        DumbService.getInstance(project).smartInvokeLater(() -> factory.initToolWindow(project, (ToolWindowEx) toolWindow));
    }
}
