package com.lsfusion.inspections;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.lsfusion.lang.LSFReferenceAnnotator;

public abstract class LSFElementVisitor extends PsiElementVisitor {

    @Override
    public void visitElement(PsiElement element) {
        if(element.getContainingFile().isWritable() && (!disabledInMeta() || !LSFReferenceAnnotator.isInMetaUsage(element)))
            visit(element);
        super.visitElement(element);
    }

    public abstract void visit(PsiElement element);

    protected boolean disabledInMeta() {
        return false;
    }
}