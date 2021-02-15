package com.lsfusion.lang.psi;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.references.LSFReference;

import java.util.function.Supplier;

public class LSFLocalSearchScope {

    public static final LSFLocalSearchScope GLOBAL = new LSFLocalSearchScope(null);

    private final Supplier<LSFFile> lsfFile;
    private final Supplier<LSFMetaCodeDeclarationStatement> metaDecl;

    public LSFLocalSearchScope(PsiElement element) {
        this(() -> element != null ? (LSFFile) element.getContainingFile() : null, () -> LSFReferenceAnnotator.getMetaDecl(element));
    }

    public LSFLocalSearchScope(Supplier<LSFFile> lsfFile, Supplier<LSFMetaCodeDeclarationStatement> metaDecl) {
        this.lsfFile = lsfFile;
        this.metaDecl = metaDecl;
    }

    public static LSFLocalSearchScope createFrom(LSFExtend extend) {
        return new LSFLocalSearchScope(extend);
    }

    public static LSFLocalSearchScope createFrom(LSFReference ref) {
        return new LSFLocalSearchScope(ref);
    }

    public static LSFLocalSearchScope createFrom(LSFDeclaration extend) {
        return new LSFLocalSearchScope(extend);
    }

    public static LSFLocalSearchScope createFrom(LSFContextFiltersClause extend) {
        return new LSFLocalSearchScope(extend);
    }

    public LSFFile getLSFFile() {
        return lsfFile.get();
    }

    public LSFMetaCodeDeclarationStatement getMetaDecl() {
        return metaDecl.get();
    }

//    todo:equals for DesignView.changeForm
}
