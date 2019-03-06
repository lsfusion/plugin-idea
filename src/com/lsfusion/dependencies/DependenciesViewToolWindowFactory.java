package com.lsfusion.dependencies;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;

public class DependenciesViewToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        DependenciesViewFactory factory = DependenciesViewFactory.getInstance();
        DumbService.getInstance(project).smartInvokeLater(() -> factory.initToolWindow(project, (ToolWindowEx) toolWindow));
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }
}
