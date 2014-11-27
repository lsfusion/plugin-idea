package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.lsfusion.refactoring.ShortenNamesProcessor;

public class ProjectCheckAbstractAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project myProject = e.getProject();
        ShortenNamesProcessor.checkGraphs(myProject);
    }
}
