package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFNavigatorElementDeclaration;
import com.simpleplugin.psi.stubs.NavigatorElementStubElement;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
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
        return AllIcons.ObjectBrowser.FlattenPackages;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.NAVIGATORELEMENT;
    }
}
