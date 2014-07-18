package com.lsfusion;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.NotNull;

public class ImplementationsSearch extends QueryExecutorBase<PsiElement, PsiElement> {

    @Override
    public void processQuery(@NotNull PsiElement sourceElement, @NotNull final Processor<PsiElement> consumer) {
        if (sourceElement instanceof LSFId) {

            final LSFDeclaration declParent = PsiTreeUtil.getParentOfType(sourceElement, LSFDeclaration.class);

            if (declParent != null) {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        for (PsiElement element : declParent.processImplementationsSearch()) {
                            consumer.process(element);
                        }
                    }
                });
            }
        }
    }
}
