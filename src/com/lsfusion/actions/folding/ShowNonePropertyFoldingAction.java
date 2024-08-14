package com.lsfusion.actions.folding;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.lsfusion.design.ui.LSFToggleAction;

public class ShowNonePropertyFoldingAction extends LSFToggleAction {
    @Override
    public boolean isSelected(AnActionEvent e) {
        return PropertyFoldingManager.isNone(getEventProject(e));
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        Project project = getEventProject(e);
        if (project != null && !PropertyFoldingManager.isNone(project)) {
            PropertyFoldingManager.setNone(project);
            PropertyFoldingManager.refreshEditor(project);
        }
    }
}
