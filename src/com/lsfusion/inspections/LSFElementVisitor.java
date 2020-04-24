package com.lsfusion.inspections;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.lsfusion.lang.LSFReferenceAnnotator;

public abstract class LSFElementVisitor extends PsiElementVisitor {

    @Override
    public void visitElement(PsiElement element) {
        // we don't want to call isInMetaUsage everytime since it accesses PSI and can lead to unnecessary parsings
        if(element.getContainingFile().isWritable() && (onlyInMeta() ? LSFReferenceAnnotator.isInMetaUsage(element) : (!disabledInMeta() || !LSFReferenceAnnotator.isInMetaUsage(element))))
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