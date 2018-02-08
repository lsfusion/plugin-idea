package com.lsfusion.dependencies.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.impl.ContentImpl;

public class ModuleDependenciesViewFactory {
    private static final ModuleDependenciesViewFactory INSTANCE = new ModuleDependenciesViewFactory();

    public static ModuleDependenciesViewFactory getInstance() {
        return INSTANCE;
    }

    public void initToolWindow(Project project, ToolWindowEx toolWindow) {
        ModuleDependenciesView view = new ModuleDependenciesView(project, toolWindow);

        ContentImpl content = new ContentImpl(view, "", true);
        toolWindow.getContentManager().addContent(content);
    }
}
