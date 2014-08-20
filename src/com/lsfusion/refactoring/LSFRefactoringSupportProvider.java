package com.lsfusion.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFId;
import org.jetbrains.annotations.NotNull;

public class LSFRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isSafeDeleteAvailable(@NotNull PsiElement element) {
        return element instanceof LSFId && LSFSafeDeleteProcessor.getElementToDelete(element) != null;
    }
}
