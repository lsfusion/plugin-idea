package com.lsfusion.dependencies;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.impl.ContentImpl;
import com.lsfusion.dependencies.module.ModuleDependenciesView;
import com.lsfusion.dependencies.property.PropertyDependenciesView;

public class DependenciesViewFactory {
    private static final DependenciesViewFactory INSTANCE = new DependenciesViewFactory();

    public static DependenciesViewFactory getInstance() {
        return INSTANCE;
    }

    public void initToolWindow(Project project, ToolWindowEx toolWindow) {
        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.add("Module dependencies", new ModuleDependenciesView(project, toolWindow));
        tabbedPane.add("Property dependencies", new PropertyDependenciesView(project, toolWindow));

        ContentImpl content = new ContentImpl(tabbedPane, "", true);
        toolWindow.getContentManager().addContent(content);
    }
}
