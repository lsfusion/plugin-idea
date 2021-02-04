package com.lsfusion.lang.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ReferenceRange;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.*;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.impl.LSFMetaCodeDeclarationStatementImpl;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.references.LSFMetaReference;
import com.lsfusion.lang.psi.references.LSFReference;
import com.lsfusion.lang.psi.stubs.impl.MetaStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFResolver implements ResolveCache.AbstractResolver<LSFReference, LSFResolveResult> {
    public static final LSFResolver INSTANCE = new LSFResolver();

    @Nullable
    @Override
    public LSFResolveResult resolve(@NotNull LSFReference reference, boolean incompleteCode) {
         return reference.resolveNoCache();
    }

    public static List<String> getIDs(LSFMetaDeclIdList idList) {
        List<String> result = new ArrayList();
        for(LSFMetaDeclId id : idList.getMetaDeclIdList())
            result.add(id.getText());
        return result;
    }
    public static Query<PsiReference> searchWordUsages(Project project, String compoundID) {
        SearchRequestCollector request = new SearchRequestCollector(new SearchSession());
        request.searchWord(compoundID, new LSFFilesSearchScope(project), UsageSearchContext.IN_CODE, true, new RequestResultProcessor() {
            @Override
            public boolean processTextOccurrence(@NotNull PsiElement element, int offsetInElement, @NotNull Processor consumer) {
                for (PsiReference ref : element.getReferences())
                    if (ReferenceRange.containsOffsetInElement(ref, offsetInElement) && !consumer.process(ref)) {
                        return false;
                    }
                return true;
            }
        });
        return new SearchRequestQuery(project, request);
    }

    public static List<LSFMetaCodeStatement> findMetaUsages(LSFMetaDeclaration metaDecl) {
        return findMetaUsages(metaDecl.getGlobalName(), metaDecl.getParamCount(), metaDecl.getLSFFile());
    }
    
    public static List<LSFMetaCodeStatement> findMetaUsages(final String name, int paramCount, final LSFFile file) {

        // песец не надежно, но что поделаешь
        final LSFMetaDeclaration virtDecl = new LSFMetaCodeDeclarationStatementImpl(new MetaStubImpl(name, paramCount, 0), LSFStubElementTypes.META) {
            public LSFFile getLSFFile() {
                return file;
            }
            public int getTextOffset() {
                return 0;
            }
        };
        final List<LSFMetaCodeStatement> result = new ArrayList<>();
        searchWordUsages(file.getProject(), name).forEach(new Processor<PsiReference>() { // на самом деле нужны только модули которые зависят от заданного файла, но не могу найти такой scope, пока не страшно если будет all
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

    public static List<LSFFullNameReference> findRenameConflicts(final String name, final LSFFullNameDeclaration decl) {
        final List<LSFFullNameReference> result = new ArrayList<>();
        searchWordUsages(decl.getProject(), name).forEach(new Processor<PsiReference>() { // на самом деле нужны только модули которые зависят от заданного файла, но не могу найти такой scope, пока не страшно если будет all
            public boolean process(PsiReference ref) {
                if (ref instanceof LSFFullNameReference && ((LSFFullNameReference<LSFDeclaration, LSFFullNameDeclaration>) ref).getFullCondition().value(decl))
                    synchronized (result) {
                        result.add((LSFFullNameReference) ref);
                    }
                return true;
            }
        });
        return result;
    }
}

