package com.simpleplugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.*;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.simpleplugin.psi.references.LSFClassReference;
import com.simpleplugin.psi.references.LSFGlobalReference;
import com.simpleplugin.psi.references.LSFMetaReference;
import com.simpleplugin.psi.references.LSFPropReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFResolver implements ResolveCache.AbstractResolver<LSFGlobalReference, List<? extends PsiElement>> {
    public static final LSFResolver INSTANCE = new LSFResolver();

    @Nullable
    @Override
    public List<? extends PsiElement> resolve(@NotNull LSFGlobalReference reference, boolean incompleteCode) {
        return new ArrayList<PsiElement>(reference.resolveNoCache().findAll());
    }

    public List<String> getStatements(LSFGlobalReference ref) {
        return new ArrayList<String>();
    }

    public static List<String> getIDs(LSFMetaDeclIdList idList) {
        List<String> result = new ArrayList();
        for(LSFMetaDeclId id : idList.getMetaDeclIdList())
            result.add(id.getText());
        return result;
    }
    public static Query<PsiReference> searchWordUsages(Project project, String compoundID) {
        SearchRequestCollector request = new SearchRequestCollector(new SearchSession());
        request.searchWord(compoundID, GlobalSearchScope.projectScope(project), UsageSearchContext.IN_CODE, true, new RequestResultProcessor() {
            public boolean processTextOccurrence(@NotNull PsiElement element, int offsetInElement, @NotNull Processor<PsiReference> consumer) {
                for (PsiReference ref : element.getReferences())
                    if (ReferenceRange.containsOffsetInElement(ref, offsetInElement) && !consumer.process(ref)) {
                        return false;
                    }
                return true;
            }
        });
        return new SearchRequestQuery(project, request);
    }

    public static List<LSFMetaCodeStatement> findMetaUsages(final Project project, final String compoundID) {
/*        final List<LSFMetaCodeStatement> result = new ArrayList<LSFMetaCodeStatement>();
        searchWordUsages(project, compoundID).forEach(new Processor<PsiReference>() {
            public boolean process(PsiReference ref) {
                if (ref instanceof LSFMetaIdUsage && ((LSFMetaIdUsage) ref).getText().equals(compoundID))
                    result.add((LSFMetaCodeStatement) ((LSFMetaIdUsage) ref).getParent());
                return true;
            }
        });
        return result;*/
        return null;
    }

}

