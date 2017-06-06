package com.lsfusion.inspections;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.lsfusion.lang.LSFReferenceAnnotator;

public abstract class LSFElementVisitor extends PsiElementVisitor {

    @Override
    public void visitElement(PsiElement element) {
        boolean isInMetaUsage = LSFReferenceAnnotator.isInMetaUsage(element);
        if(element.getContainingFile().isWritable() && (onlyInMeta() ? isInMetaUsage : (!disabledInMeta() || !isInMetaUsage)))
            visit(element);
        super.visitElement(element);
    }

    public abstract void visit(PsiElement element);

    protected boolean disabledInMeta() {
        return false;
    }

    protected boolean onlyInMeta() {
        return false;
    }
}