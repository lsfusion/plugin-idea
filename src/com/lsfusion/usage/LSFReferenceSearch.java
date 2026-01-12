package com.lsfusion.usage;

import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import com.lsfusion.lang.psi.declarations.SyntheticPropertyStatement;
import com.lsfusion.lang.psi.declarations.impl.LSFStatementActionDeclarationImpl;
import org.jetbrains.annotations.NotNull;

public class LSFReferenceSearch
        implements QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {

    @Override
    public boolean execute(@NotNull ReferencesSearch.SearchParameters params,
                           @NotNull Processor<? super PsiReference> processor) {

        return ReadAction.compute(() -> {
            PsiElement target = params.getElementToSearch();
            LSFStatementActionDeclarationImpl actionDecl = PsiTreeUtil.getParentOfType(target, LSFStatementActionDeclarationImpl.class);
            if (actionDecl != null) {
                SyntheticPropertyStatement syntheticProperty = actionDecl.getSyntheticProperty();
                if (syntheticProperty != null && !target.equals(syntheticProperty)) {
                    return ReferencesSearch.search(syntheticProperty, params.getEffectiveSearchScope(), params.isIgnoreAccessScope()).forEach(processor);
                }
            }
            return true;
        });
    }
}