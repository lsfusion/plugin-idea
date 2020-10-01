package com.lsfusion;

import com.intellij.codeInsight.TargetElementEvaluatorEx2;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFTargetElementEvaluatorEx2 extends TargetElementEvaluatorEx2 {
    @Override
    public boolean isAcceptableNamedParent(@NotNull PsiElement parent) {
        LSFDeclaration declParent = PsiTreeUtil.getParentOfType(parent, LSFDeclaration.class);
        return declParent != null && parent.equals(declParent.getNameIdentifier());
    }

    // for backward compatibility
    @Nullable
    @Override
    public PsiElement getElementByReference(@NotNull PsiReference psiReference, int i) {
        return null;
    }
}
