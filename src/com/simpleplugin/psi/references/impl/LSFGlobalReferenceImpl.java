package com.simpleplugin.psi.references.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;
import com.simpleplugin.psi.references.LSFGlobalReference;
import com.simpleplugin.psi.LSFResolveUtil;
import com.simpleplugin.psi.LSFResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class LSFGlobalReferenceImpl<T extends LSFGlobalDeclaration> extends LSFReferenceImpl implements LSFGlobalReference<T> {

    protected LSFGlobalReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isSoft() {
        return false;
    }

    @Override
    public String getNameRef() {
        return getSimpleName().getText();
    }

    @Override
    public LSFId resolve() {
        T decl = resolveDecl();
        if(decl == null)
            return null;
        return decl.getNameIdentifier();
    }

    public T resolveDecl() {
        final ResolveResult[] resolveResults = multiResolveDecl(true);

        return resolveResults.length == 0 ||
                resolveResults.length > 1 ||
                !resolveResults[0].isValidResult() ? null : (T) resolveResults[0].getElement();
    }

    @NotNull
    public ResolveResult[] multiResolveDecl(boolean incompleteCode) {
        final List<? extends PsiElement> elements =
                ResolveCache.getInstance(getProject()).resolveWithCaching(this, LSFResolver.INSTANCE, true, incompleteCode);
        return LSFResolveUtil.toCandidateInfoArray(elements);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<String> elements = LSFResolver.INSTANCE.getStatements(this);
        List<LookupElement> variants = new ArrayList<LookupElement>();
        for (final String property : elements) {
            variants.add(LookupElementBuilder.create(property).
                    withIcon(LSFIcons.FILE).
                    withTypeText(getLSFFile().getName())
            );
        }
        return variants.toArray();
    }

}
