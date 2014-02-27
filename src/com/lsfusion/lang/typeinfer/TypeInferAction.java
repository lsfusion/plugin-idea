package com.lsfusion.typeinfer;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.psi.context.ModifyParamContext;
import org.jetbrains.annotations.NotNull;

public class TypeInferAction implements IntentionAction {

    private ModifyParamContext modify;
    
    public TypeInferAction(ModifyParamContext modify) {
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
        TypeInferer.typeInfer(modify, null);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
