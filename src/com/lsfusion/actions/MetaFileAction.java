package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.psi.LSFFile;

public abstract class MetaFileAction extends AnAction {
    
    protected LSFFile getLSFFile(AnActionEvent e) {
        Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());
        if(editor != null) {
            PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, e.getProject());
            if(psiFile instanceof LSFFile)
                return (LSFFile) psiFile;
        }
        return null;
    }
    protected abstract boolean isEnabled();

    @Override
    public void actionPerformed(AnActionEvent e) {
        LSFFile file = getLSFFile(e);
        if(file!=null)
            MetaChangeDetector.getInstance(e.getProject()).reprocessFile(file, isEnabled());
    }
}
