package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.psi.LSFId;
import com.lsfusion.psi.LSFSimpleNameWithCaption;
import com.lsfusion.psi.declarations.LSFGroupDeclaration;
import com.lsfusion.psi.stubs.GroupStubElement;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
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
}
