package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.psi.LSFFile;

public abstract class MetaFileAction extends AnAction {
    
    protected LSFFile getLSFFile(Project project, AnActionEvent e) {
        Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());
        if(editor != null) {
            PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
            if(psiFile instanceof LSFFile)
                return (LSFFile) psiFile;
        }
        return null;
    }
    protected abstract boolean isEnabled();

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            LSFFile file = getLSFFile(project, e);
            if (file != null) {
                MetaChangeDetector.getInstance(project).reprocessFile(file, isEnabled());
            }
        }
    }
}
