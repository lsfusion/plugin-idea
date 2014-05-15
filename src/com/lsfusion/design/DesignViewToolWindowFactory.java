package com.lsfusion.design;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;

public class DesignViewToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        DesignViewFactory factory = DesignViewFactory.getInstance();
        factory.initToolWindow(project, (ToolWindowEx) toolWindow);
    }
}
