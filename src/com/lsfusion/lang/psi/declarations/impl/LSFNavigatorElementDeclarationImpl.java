package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.psi.LSFId;
import com.lsfusion.psi.LSFSimpleName;
import com.lsfusion.psi.declarations.LSFNavigatorElementDeclaration;
import com.lsfusion.psi.stubs.NavigatorElementStubElement;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFNavigatorElementDeclarationImpl extends LSFFullNameDeclarationImpl<LSFNavigatorElementDeclaration, NavigatorElementStubElement> implements LSFNavigatorElementDeclaration {

    protected LSFNavigatorElementDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFNavigatorElementDeclarationImpl(@NotNull NavigatorElementStubElement navigatorElementStubElement, @NotNull IStubElementType nodeType) {
        super(navigatorElementStubElement, nodeType);
    }

    protected abstract LSFSimpleName getSimpleName();

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.NAVIGATOR_ELEMENT;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.NAVIGATORELEMENT;
    }
}
