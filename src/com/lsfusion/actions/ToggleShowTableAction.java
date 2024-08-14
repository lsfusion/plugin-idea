package com.lsfusion.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.util.LocalTimeCounter;
import com.lsfusion.design.ui.LSFToggleAction;

import java.util.Collections;

public class ToggleShowTableAction extends LSFToggleAction {
    public static final String LSF_PROPERTY_SHOW_TABLE_ON = "lsfusion.property.show.table.on";

    @Override
    public boolean isSelected(AnActionEvent e) {
        return isShowTableEnabled(getEventProject(e));
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        final Project project = getEventProject(e);
        if (project != null) {
            setShowTableEnabled(project, !isShowTableEnabled(project));

            final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
                ((DocumentImpl) editor.getDocument()).setModificationStamp(LocalTimeCounter.currentTime());
                PsiDocumentManager.getInstance(project).reparseFiles(Collections.singleton(((EditorImpl) editor).getVirtualFile()), true);
            }
        }
    }
    
    public static boolean isShowTableEnabled(Project project) {
        return project != null && PropertiesComponent.getInstance(project).getBoolean(LSF_PROPERTY_SHOW_TABLE_ON, true);
    }

    public static void setShowTableEnabled(Project project, boolean enabled) {
        PropertiesComponent.getInstance(project).setValue(LSF_PROPERTY_SHOW_TABLE_ON, Boolean.toString(enabled));
    }
}