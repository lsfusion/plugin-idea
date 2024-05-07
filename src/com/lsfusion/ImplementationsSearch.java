package com.lsfusion;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.NotNull;

public class ImplementationsSearch extends QueryExecutorBase<PsiElement, PsiElement> {
    @Override
    public void processQuery(@NotNull PsiElement sourceElement, @NotNull Processor consumer) {
        if (sourceElement instanceof LSFId) {
            ApplicationManager.getApplication().runReadAction(() -> ProgressManager.getInstance().runProcess(() -> {
                LSFDeclaration declParent = PsiTreeUtil.getParentOfType(sourceElement, LSFDeclaration.class);
                if (declParent != null) {
                    for (PsiElement element : declParent.processImplementationsSearch()) {
                        consumer.process(element);
                    }
                }
            }, new BackgroundableProcessIndicator(sourceElement.getProject(), "Implementations Search", "cancel", "stop", false)));
        }
    }
}
