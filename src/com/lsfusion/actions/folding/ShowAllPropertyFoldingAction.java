package com.lsfusion.actions.folding;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.lsfusion.design.ui.LSFToggleAction;

public class ShowAllPropertyFoldingAction extends LSFToggleAction {
    @Override
    public boolean isSelected(AnActionEvent e) {
        return PropertyFoldingManager.isAll(getEventProject(e));
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        Project project = getEventProject(e);
        if (project != null && !PropertyFoldingManager.isAll(project)) {
            PropertyFoldingManager.setAll(project);
            PropertyFoldingManager.refreshEditor(project);
        }
    }
}
