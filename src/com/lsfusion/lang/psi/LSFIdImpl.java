package com.lsfusion.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.LSFElementGenerator;
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
