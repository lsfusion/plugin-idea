package com.lsfusion.dependencies;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;

public class ModuleDependeciesViewToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ModuleDependenciesViewFactory factory = ModuleDependenciesViewFactory.getInstance();
        factory.initToolWindow(project, (ToolWindowEx) toolWindow);
    }
}
