package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.lsfusion.design.ui.LSFToggleAction;

import static com.lsfusion.completion.CompletionUtils.isCompletionEnabled;
import static com.lsfusion.completion.CompletionUtils.setCompletionEnabled;

public class ToggleCompletionAction extends LSFToggleAction {
    @Override
    public boolean isSelected(AnActionEvent e) {
        return isCompletionEnabled(getEventProject(e));
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        Project project = getEventProject(e);
        if (project != null) {
            setCompletionEnabled(project, !isCompletionEnabled(project));
        }
    }
}