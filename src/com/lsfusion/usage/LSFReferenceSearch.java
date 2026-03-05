package com.lsfusion.usage;

import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import org.jetbrains.annotations.NotNull;

public class LSFReferenceSearch
        implements QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {

    @Override
    public boolean execute(@NotNull ReferencesSearch.SearchParameters params,
                           @NotNull Processor<? super PsiReference> processor) {
        return true;
    }
}