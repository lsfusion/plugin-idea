package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.meta.MetaTransaction;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class LSFIdImpl extends ASTWrapperPsiElement implements LSFId {

    public LSFIdImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public void setName(String name, MetaTransaction transaction) {
        LSFId genId = LSFElementGenerator.createIdentifierFromText(getProject(), name);

        if(transaction != null)
            transaction.regChange(Collections.singletonList(genId.getNode()), getNode(), MetaTransaction.Type.REPLACE);

        getNode().getTreeParent().replaceChild(getNode(), genId.getNode());
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        setName(name, null);
        return this;
    }

    @Override
    public ItemPresentation getPresentation() {
        LSFDeclaration parentDecl = PsiTreeUtil.getParentOfType(this, LSFDeclaration.class);
        return parentDecl == null ? null : parentDecl;
    }
}
