package com.lsfusion.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.util.LocalTimeCounter;

import java.util.Collections;

public class ToggleComplexityAction extends ToggleAction {
    public static final String LSF_PROPERTY_COMPLEXITY_ON = "lsfusion.property.complexity.on";

    @Override
    public boolean isSelected(AnActionEvent e) {
        return isComplexityEnabled(getEventProject(e));
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        final Project project = e.getProject();
        if (project != null) {
            setComplexityEnabled(project, !isComplexityEnabled(project));

            final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
                ((DocumentImpl) editor.getDocument()).setModificationStamp(LocalTimeCounter.currentTime());
                PsiDocumentManager.getInstance(project).reparseFiles(Collections.singleton(((EditorImpl) editor).getVirtualFile()), true);
            }
        }
    }
    
    public static boolean isComplexityEnabled(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(LSF_PROPERTY_COMPLEXITY_ON, true);
    }

    public static void setComplexityEnabled(Project project, boolean enabled) {
        PropertiesComponent.getInstance(project).setValue(LSF_PROPERTY_COMPLEXITY_ON, Boolean.toString(enabled));
    }
}