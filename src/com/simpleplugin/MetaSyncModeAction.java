package com.simpleplugin;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.SizedIcon;
import com.intellij.util.ui.EmptyIcon;
import com.simpleplugin.psi.MetaChangeDetector;

import javax.swing.*;

public class MetaSyncModeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        MetaChangeDetector.getInstance(e.getProject()).toggleMetaSyncMode();
    }

    public static final Icon CHECKED_ICON = new SizedIcon(AllIcons.Actions.Checked, 16, 16);
    public static final Icon EMPTY_ICON = EmptyIcon.ICON_16;

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setIcon(MetaChangeDetector.getInstance(e.getProject()).getMetaSyncMode() ? CHECKED_ICON : EMPTY_ICON);
    }
}