package com.lsfusion.dependencies.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;

public class ModuleDependenciesViewFactory {
    private static final ModuleDependenciesViewFactory INSTANCE = new ModuleDependenciesViewFactory();

    public static ModuleDependenciesViewFactory getInstance() {
        return INSTANCE;
    }

    public void initToolWindow(Project project, ToolWindowEx toolWindow) {
        ModuleDependenciesView view = new ModuleDependenciesView(project, toolWindow);

        toolWindow.getComponent().removeAll();
        toolWindow.getComponent().add(view);
        toolWindow.getComponent().repaint();
    }
}
