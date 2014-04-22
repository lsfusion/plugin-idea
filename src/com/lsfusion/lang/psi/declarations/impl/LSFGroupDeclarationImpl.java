package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleNameWithCaption;
import com.lsfusion.lang.psi.LSFStringLiteral;
import com.lsfusion.lang.psi.declarations.LSFGroupDeclaration;
import com.lsfusion.lang.psi.stubs.GroupStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFGroupDeclarationImpl extends LSFFullNameDeclarationImpl<LSFGroupDeclaration, GroupStubElement> implements LSFGroupDeclaration {

    public LSFGroupDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFGroupDeclarationImpl(@NotNull GroupStubElement groupStubElement, @NotNull IStubElementType nodeType) {
        super(groupStubElement, nodeType);
    }

    protected abstract LSFSimpleNameWithCaption getSimpleNameWithCaption();

    @Override
    public LSFId getNameIdentifier() {
        return getSimpleNameWithCaption().getSimpleName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.GROUP;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.GROUP;
    }

    @Override
    public String getGroupName() {
        LSFId nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getName() : null;
    }

    @Override
    public String getCaption() {
        LSFStringLiteral literal = getSimpleNameWithCaption().getStringLiteral();
        return literal != null ? literal.getValue() : null;
    }
}
