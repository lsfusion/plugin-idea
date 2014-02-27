package com.lsfusion.lang.typeinfer;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import org.jetbrains.annotations.NotNull;

public class MetaTypeInferAction implements IntentionAction {

    private LSFMetaDeclaration modify;

    public MetaTypeInferAction(LSFMetaDeclaration modify) {
        this.modify = modify;
    }

    @NotNull
    @Override
    public String getText() {
        return "Infer type";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return modify.isValid();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        MetaTransaction transaction = new MetaTransaction();

        TypeInferer.metaTypeInfer(modify, transaction);
        
        transaction.apply();
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
