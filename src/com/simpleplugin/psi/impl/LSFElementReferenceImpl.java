package com.simpleplugin.psi.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class LSFElementReferenceImpl extends LSFReferenceImpl implements LSFElementReference {

    protected LSFElementReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isSoft() {
        return false;
    }

    @Override
    public PsiElement resolve() {
        final ResolveResult[] resolveResults = multiResolve(true);

        return resolveResults.length == 0 ||
                resolveResults.length > 1 ||
                !resolveResults[0].isValidResult() ? null : resolveResults[0].getElement();
    }

    @NotNull
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final List<? extends PsiElement> elements =
                ResolveCache.getInstance(getProject()).resolveWithCaching(this, LSFResolver.INSTANCE, true, incompleteCode);
        return LSFResolveUtil.toCandidateInfoArray(elements);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LSFSimpleNameWithCaption> elements = LSFResolver.INSTANCE.getStatements(getContainingFile(), this instanceof LSFClassReference);
        List<LookupElement> variants = new ArrayList<LookupElement>();
        for (final LSFSimpleNameWithCaption property : elements) {
            variants.add(LookupElementBuilder.create(property).
                    withIcon(LSFIcons.FILE).
                    withTypeText(property.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }

}
