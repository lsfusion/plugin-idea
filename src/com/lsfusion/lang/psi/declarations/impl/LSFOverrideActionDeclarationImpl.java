package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFOverrideActionDeclaration;
import com.lsfusion.lang.psi.stubs.OverrideActionStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class LSFOverrideActionDeclarationImpl extends LSFFullNameDeclarationImpl<LSFOverrideActionDeclaration, OverrideActionStubElement> implements LSFOverrideActionDeclaration {

    protected LSFOverrideActionDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFOverrideActionDeclarationImpl(@NotNull OverrideActionStubElement overrideActionStubElement, @NotNull IStubElementType nodeType) {
        super(overrideActionStubElement, nodeType);
    }

    private LSFSimpleName getSimpleName() {
        Collection<PsiElement> result = LSFPsiUtils.findChildrenOfType(this, LSFSimpleName.class);
        for (PsiElement psiElement : result) {
            return (LSFSimpleName) psiElement;
        }
        return null;
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.PROPERTY;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.OVERRIDEACTION;
    }
}