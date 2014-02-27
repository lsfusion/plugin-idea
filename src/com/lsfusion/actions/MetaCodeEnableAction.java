package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lsfusion.meta.MetaChangeDetector;

public class MetaCodeEnableAction extends AnAction {
             
    @Override
    public void actionPerformed(AnActionEvent e) {
        MetaChangeDetector.getInstance(e.getProject()).toggleMetaEnabled();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setText(MetaChangeDetector.getInstance(e.getProject()).getMetaEnabled()? "Disable _meta" : "Enable _meta");
    }
}
