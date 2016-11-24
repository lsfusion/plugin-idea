package com.lsfusion.actions.folding;

import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.LocalTimeCounter;
import com.lsfusion.lang.folding.LSFPropertyParamsFoldingManager;

public class PropertyFoldingManager {
    private static final String LSF_PROPERTY_FOLDING_STATE = "lsfusion.property.folding";

    public static FoldingMode getFoldingMode(Project project) {
        return project == null ? null :  FoldingMode.valueOf(PropertiesComponent.getInstance(project).getValue(LSF_PROPERTY_FOLDING_STATE, FoldingMode.IMPLICIT.name()));
    }

    public static void setFoldingMode(Project project, FoldingMode foldingMode) {
        if (project != null) {
            PropertiesComponent.getInstance(project).setValue(LSF_PROPERTY_FOLDING_STATE, foldingMode.name());
        }
    }

    public static boolean isAll(Project project) {
        return getFoldingMode(project) == FoldingMode.ALL;
    }

    public static void setAll(Project project) {
        setFoldingMode(project, FoldingMode.ALL);
    }

    public static boolean isNone(Project project) {
        return getFoldingMode(project) == FoldingMode.NONE;
    }

    public static void setNone(Project project) {
        setFoldingMode(project, FoldingMode.NONE);
    }

    public static boolean isImplicit(Project project) {
        return getFoldingMode(project) == FoldingMode.IMPLICIT;
    }

    public static void setImplicit(Project project) {
        setFoldingMode(project, FoldingMode.IMPLICIT);
    }

    public static void refreshEditor(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            final Document document = editor.getDocument();
            ((DocumentImpl) document).setModificationStamp(LocalTimeCounter.currentTime());
            CodeFoldingManager.getInstance(project).updateFoldRegions(editor);
            LSFPropertyParamsFoldingManager.foldingsRebuilt(document);
        }
    }

    private enum FoldingMode {
        ALL, NONE, IMPLICIT
    }
}
