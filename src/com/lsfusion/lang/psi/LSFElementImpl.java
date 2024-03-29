package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

public class LSFElementImpl extends ASTWrapperPsiElement implements LSFElement {

    public LSFElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public static boolean isCorrect(LSFElement element){
        return !PsiUtilCore.hasErrorElementChild(element);
    }
    @Override
    public boolean isCorrect() {
        return isCorrect(this);
    }

    @Override
    public LSFFile getLSFFile() {
        return (LSFFile) getContainingFile();
    }
    
    @NotNull
    @Override
    public Project getProject() {
        return getLSFFile().getProject();
    }
}
