package com.simpleplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.simpleplugin.psi.MetaChangeDetector;

public class MetaHideFileAction extends MetaFileAction {

    @Override
    protected boolean isEnabled() {
        return false;
    }
}
