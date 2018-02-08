package com.lsfusion.dependencies.property;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.impl.ContentImpl;

public class PropertyDependenciesViewFactory {
    private static final PropertyDependenciesViewFactory INSTANCE = new PropertyDependenciesViewFactory();

    public static PropertyDependenciesViewFactory getInstance() {
        return INSTANCE;
    }

    public void initToolWindow(Project project, ToolWindowEx toolWindow) {
        PropertyDependenciesView view = new PropertyDependenciesView(project, toolWindow);

        ContentImpl content = new ContentImpl(view, "", true);
        toolWindow.getContentManager().addContent(content);
    }
}
