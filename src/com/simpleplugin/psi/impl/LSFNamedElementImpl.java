package com.simpleplugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.simpleplugin.LSFElementGenerator;
import com.simpleplugin.psi.LSFCompoundID;
import com.simpleplugin.psi.LSFNamedElement;
import com.simpleplugin.psi.LSFTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFNamedElementImpl extends ASTWrapperPsiElement implements LSFNamedElement {

    public LSFNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(LSFTypes.COMPOUND_ID);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        PsiElement keyNode = findChildByType(LSFTypes.COMPOUND_ID);
        if (keyNode != null) {
            LSFCompoundID genId = LSFElementGenerator.createIdentifierFromText(getProject(), name);
            getNode().replaceChild(keyNode.getNode(), genId.getNode());
        }
        return this;
    }
}
