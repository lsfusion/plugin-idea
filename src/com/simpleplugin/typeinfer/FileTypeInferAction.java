package com.simpleplugin.typeinfer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.simpleplugin.meta.MetaTransaction;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.MetaChangeDetector;

public class FileTypeInferAction extends AnAction {

    protected LSFFile getLSFFile(AnActionEvent e) {
        Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());
        if(editor != null) {
            PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, e.getProject());
            if(psiFile instanceof LSFFile)
                return (LSFFile) psiFile;
        }
        return null;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final LSFFile file = getLSFFile(e);
        if(file!=null) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    MetaTransaction transaction = new MetaTransaction();
                    TypeInferer.typeInfer(file, transaction);
                    transaction.apply();
                }
            });
        }
    }

}
