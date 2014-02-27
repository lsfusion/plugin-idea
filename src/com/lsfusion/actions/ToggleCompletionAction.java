package com.lsfusion.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SizedIcon;
import com.intellij.util.ui.EmptyIcon;

import javax.swing.*;

import static com.lsfusion.completion.CompletionUtils.isCompletionEnabled;
import static com.lsfusion.completion.CompletionUtils.setCompletionEnabled;

public class ToggleCompletionAction extends AnAction {

    public static final Icon CHECKED_ICON = new SizedIcon(AllIcons.Actions.Checked, 16, 16);
    public static final Icon EMPTY_ICON = EmptyIcon.ICON_16;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            setCompletionEnabled(project, !isCompletionEnabled(project));
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        Presentation presentation = e.getPresentation();
        if (project == null) {
            presentation.setEnabled(false);
            return;
        }

        presentation.setEnabled(true);
        presentation.setIcon(isCompletionEnabled(project) ? CHECKED_ICON : EMPTY_ICON);
    }
}