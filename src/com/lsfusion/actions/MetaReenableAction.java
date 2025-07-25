package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.lsfusion.lang.meta.MetaChangeDetector;

import javax.swing.*;

public class MetaReenableAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            MetaChangeDetector metaChangeDetector = MetaChangeDetector.getInstance(project);
            if (metaChangeDetector.getMetaEnabled()) {
                metaChangeDetector.reenableAllMetaCodes();
            } else {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Meta code is disabled. The action won't be performed", "Reenabling meta", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
