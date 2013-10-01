package com.simpleplugin.psi;

import com.intellij.openapi.module.Module;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.*;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.impl.LSFMetaCodeDeclarationStatementImpl;
import com.simpleplugin.psi.references.LSFGlobalReference;
import com.simpleplugin.psi.references.LSFMetaReference;
import com.simpleplugin.psi.references.LSFReference;
import com.simpleplugin.psi.stubs.impl.MetaStubImpl;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFResolver implements ResolveCache.AbstractResolver<LSFReference, List<? extends PsiElement>> {
    public static final LSFResolver INSTANCE = new LSFResolver();

    @Nullable
    @Override
    public List<? extends PsiElement> resolve(@NotNull LSFReference reference, boolean incompleteCode) {
        return new ArrayList<PsiElement>(reference.resolveNoCache().findAll());
    }

    public static List<String> getIDs(LSFMetaDeclIdList idList) {
        List<String> result = new ArrayList();
        for(LSFMetaDeclId id : idList.getMetaDeclIdList())
            result.add(id.getText());
        return result;
    }
    public static Query<PsiReference> searchWordUsages(GlobalSearchScope scope, String compoundID) {
        SearchRequestCollector request = new SearchRequestCollector(new SearchSession());
        request.searchWord(compoundID, scope, UsageSearchContext.IN_CODE, true, new RequestResultProcessor() {
            public boolean processTextOccurrence(@NotNull PsiElement element, int offsetInElement, @NotNull Processor<PsiReference> consumer) {
                for (PsiReference ref : element.getReferences())
                    if (ReferenceRange.containsOffsetInElement(ref, offsetInElement) && !consumer.process(ref)) {
                        return false;
                    }
                return true;
            }
        });
        return new SearchRequestQuery(scope.getProject(), request);
    }

    public static List<LSFMetaCodeStatement> findMetaUsages(LSFMetaDeclaration metaDecl) {
        return findMetaUsages(metaDecl.getGlobalName(), metaDecl.getParamCount(), metaDecl.getLSFFile());
    }
    
    public static List<LSFMetaCodeStatement> findMetaUsages(final String name, int paramCount, final LSFFile file) {

        // песец не надежно, но что поделаешь
        final LSFMetaDeclaration virtDecl = new LSFMetaCodeDeclarationStatementImpl(new MetaStubImpl(name, paramCount), LSFStubElementTypes.META) {
            public LSFFile getLSFFile() {
                return file;
            }
        };
        final List<LSFMetaCodeStatement> result = new ArrayList<LSFMetaCodeStatement>();
        searchWordUsages(GlobalSearchScope.allScope(file.getProject()), name).forEach(new Processor<PsiReference>() { // на самом деле нужны только модули которые зависят от заданного файла, но не могу найти такой scope, пока не страшно если будет all
            public boolean process(PsiReference ref) {
                if (ref instanceof LSFMetaReference && ((LSFMetaReference) ref).isResolveToVirt(virtDecl))
                    synchronized (result) {
                        result.add((LSFMetaCodeStatement) ref);
                    }
                return true;
            }
        });
        return result;
    }

}

