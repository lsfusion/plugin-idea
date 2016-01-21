package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.lsfusion.lang.meta.MetaChangeDetector;

import javax.swing.*;

public class MetaCodeEnableAction extends AnAction {
             
    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = getEventProject(e); // not to share context
        int result = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(), 
                "Are you sure you want to toggle meta code mode?", "Confirm Meta Action", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            MetaChangeDetector.getInstance(project).toggleMetaEnabled();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        if (e.getProject() != null) {
            e.getPresentation().setText(MetaChangeDetector.getInstance(e.getProject()).getMetaEnabled() ? "Disable _meta" : "Enable _meta");
        }
    }
}
