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

public class ToggleHighlightWarningsAction extends ToggleAction {
    public static final String LSF_PROPERTY_HIGHLIGHT_WARNINGS_ON = "lsfusion.property.highlight.warnings.on";

    @Override
    public boolean isSelected(AnActionEvent e) {
        return isHighlightWarningsEnabled(getEventProject(e));
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        final Project project = getEventProject(e);
        if (project != null) {
            setHighlightWarningsEnabled(project, !isHighlightWarningsEnabled(project));

            final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
                ((DocumentImpl) editor.getDocument()).setModificationStamp(LocalTimeCounter.currentTime());
                PsiDocumentManager.getInstance(project).reparseFiles(Collections.singleton(((EditorImpl) editor).getVirtualFile()), true);
            }
        }
    }
    
    public static boolean isHighlightWarningsEnabled(Project project) {
        return project != null && PropertiesComponent.getInstance(project).getBoolean(LSF_PROPERTY_HIGHLIGHT_WARNINGS_ON, true);
    }

    public static void setHighlightWarningsEnabled(Project project, boolean enabled) {
        PropertiesComponent.getInstance(project).setValue(LSF_PROPERTY_HIGHLIGHT_WARNINGS_ON, Boolean.toString(enabled));
    }
}