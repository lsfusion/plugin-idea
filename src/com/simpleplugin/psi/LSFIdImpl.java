package com.simpleplugin.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import com.simpleplugin.LSFElementGenerator;
import com.simpleplugin.psi.declarations.impl.LSFDeclarationImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LSFIdImpl extends ASTWrapperPsiElement implements LSFId {

    public LSFIdImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        LSFId genId = LSFElementGenerator.createIdentifierFromText(getProject(), name);
        getNode().getTreeParent().replaceChild(getNode(), genId.getNode());
        return this;
    }
}
