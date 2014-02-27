package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lsfusion.lang.meta.MetaChangeDetector;

public class MetaRefreshAction extends AnAction {
    
    @Override
    public void actionPerformed(AnActionEvent e) {
        MetaChangeDetector.getInstance(e.getProject()).reprocessAllDocuments();
    }
}
