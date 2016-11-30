package com.lsfusion.actions.folding;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.LocalTimeCounter;
import com.lsfusion.lang.folding.LSFPropertyParamsFoldingManager;

import static com.lsfusion.actions.folding.PropertyFoldingManager.FoldingMode.*;

public class PropertyFoldingManager {
    private static final String LSF_PROPERTY_FOLDING_STATE = "lsfusion.property.folding";

    public static FoldingMode getFoldingMode(Project project) {
        return project == null ? null : FoldingMode.valueOf(PropertiesComponent.getInstance(project).getValue(LSF_PROPERTY_FOLDING_STATE, IMPLICIT.name()));
    }

    public static void setFoldingMode(Project project, FoldingMode foldingMode) {
        if (project != null) {
            PropertiesComponent.getInstance(project).setValue(LSF_PROPERTY_FOLDING_STATE, foldingMode.name());
        }
    }

    public static boolean isAll(Project project) {
        return getFoldingMode(project) == ALL;
    }

    public static void setAll(Project project) {
        setFoldingMode(project, ALL);
    }

    public static boolean isNone(Project project) {
        return getFoldingMode(project) == NONE;
    }

    public static void setNone(Project project) {
        setFoldingMode(project, NONE);
    }

    public static boolean isImplicit(Project project) {
        return getFoldingMode(project) == IMPLICIT;
    }

    public static void setImplicit(Project project) {
        setFoldingMode(project, IMPLICIT);
    }

    public static void refreshEditor(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            ((DocumentImpl) editor.getDocument()).setModificationStamp(LocalTimeCounter.currentTime());
            LSFPropertyParamsFoldingManager.updateFoldRegions(editor);
        }
    }

    enum FoldingMode {
        ALL, NONE, IMPLICIT
    }
}
