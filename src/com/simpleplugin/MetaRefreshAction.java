package com.simpleplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.simpleplugin.psi.MetaChangeDetector;

public class MetaRefreshAction extends AnAction {
    
    @Override
    public void actionPerformed(AnActionEvent e) {
        MetaChangeDetector.getInstance(e.getProject()).reprocessAllDocuments();
    }
}
