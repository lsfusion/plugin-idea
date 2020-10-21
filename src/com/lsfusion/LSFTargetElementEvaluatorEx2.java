package com.lsfusion;

import com.intellij.codeInsight.TargetElementEvaluatorEx2;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.LSFExprParameterUsage;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFTargetElementEvaluatorEx2 extends TargetElementEvaluatorEx2 {
    @Override
    public boolean isAcceptableNamedParent(@NotNull PsiElement parent) {
        LSFExprParameterUsage paramUsageParent = PsiTreeUtil.getParentOfType(parent, LSFExprParameterUsage.class);
        if (paramUsageParent != null) {
            return false;
        }
        LSFDeclaration declParent = PsiTreeUtil.getParentOfType(parent, LSFDeclaration.class);
        return declParent != null &&
                !(declParent instanceof LSFPropertyDrawDeclaration) &&
                parent.equals(declParent.getNameIdentifier());
    }

    // for backward compatibility
    @Nullable
    @Override
    public PsiElement getElementByReference(@NotNull PsiReference psiReference, int i) {
        return null;
    }
}
