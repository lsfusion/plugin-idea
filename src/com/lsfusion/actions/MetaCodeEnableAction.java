package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.lsfusion.lang.meta.MetaChangeDetector;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MetaCodeEnableAction extends AnAction {
             
    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = getEventProject(e); // not to share context
        
        boolean doToggle = true;
        if (e.getInputEvent() instanceof KeyEvent) {
            boolean enabled = MetaChangeDetector.getInstance(project).getMetaEnabled();
            int result = JOptionPane.showConfirmDialog(
                    JOptionPane.getRootFrame(),
                    "Are you sure you want to " + (enabled ? "disable" : "enable") + " meta code?", 
                    (enabled ? "Disable" : "Enable") + " Meta Action", 
                    JOptionPane.YES_NO_OPTION
            );
            if (result != JOptionPane.OK_OPTION) {
                doToggle = false;
            }
        }
        if (doToggle) {
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
