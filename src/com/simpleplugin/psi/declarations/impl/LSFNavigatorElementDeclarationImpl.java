package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFNavigatorElementDeclaration;
import com.simpleplugin.psi.stubs.NavigatorElementStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
