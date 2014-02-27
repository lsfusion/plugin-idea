package com.lsfusion;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import com.lsfusion.psi.LSFId;
import com.lsfusion.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.NotNull;

//import com.intellij.psi.search.searches.DefinitionsScopedSearch;

public class ImplementationsSearch extends QueryExecutorBase<PsiElement, PsiElement> {

    @Override
    public void processQuery(@NotNull PsiElement queryParameters, @NotNull final Processor<PsiElement> consumer) {
        final PsiElement sourceElement = queryParameters;
        if (sourceElement instanceof LSFId) {

            PsiElement nearestExtendableParent = sourceElement;
            while (nearestExtendableParent.getParent() != null) {
                if (nearestExtendableParent instanceof LSFDeclaration) {
                    break;
                }
                nearestExtendableParent = nearestExtendableParent.getParent();
            }

            if (nearestExtendableParent.getParent() != null) {
                final LSFDeclaration finalNearestExtendableParent = (LSFDeclaration) nearestExtendableParent;

                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        for (PsiElement element : finalNearestExtendableParent.processImplementationsSearch()) {
                            consumer.process(element);
                        }
                    }
                });
            }
        }
    }
}
