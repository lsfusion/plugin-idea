package com.lsfusion.dependencies.property;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;

public class PropertyDependenciesViewToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        PropertyDependenciesViewFactory factory = PropertyDependenciesViewFactory.getInstance();
        factory.initToolWindow(project, (ToolWindowEx) toolWindow);    
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }
}
