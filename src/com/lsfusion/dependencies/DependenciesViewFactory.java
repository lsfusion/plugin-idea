package com.lsfusion.dependencies;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
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

        UMLDiagramView umlDiagramView = new UMLDiagramView(project);
        tabbedPane.add("Class Diagram", umlDiagramView.getComponent());
        ModuleDependenciesView moduleView = new ModuleDependenciesView(project, toolWindow);
        tabbedPane.add("Module dependencies", moduleView);
        PropertyDependenciesView propertyView = new PropertyDependenciesView(project, toolWindow);
        tabbedPane.add("Property dependencies", propertyView);

        tabbedPane.addChangeListener(e -> {
            if(tabbedPane.getSelectedIndex() == 0) {
                umlDiagramView.redraw();
            } else {
                ((DependenciesView) tabbedPane.getSelectedComponent()).redraw();
            }
        });

        ContentImpl content = new ContentImpl(tabbedPane, "", true);
        // Dispose the tab views with the tool window content (project close): their application-level
        // ActionManager timer listeners, update-timer threads and the JCEF browser must not outlive
        // the project.
        content.setDisposer(() -> {
            Disposer.dispose(umlDiagramView);
            Disposer.dispose(moduleView);
            Disposer.dispose(propertyView);
        });
        toolWindow.getContentManager().addContent(content);
    }
}
