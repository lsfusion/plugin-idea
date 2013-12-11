package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFWindowDeclaration;
import com.simpleplugin.psi.stubs.WindowStubElement;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFWindowDeclarationImpl extends LSFFullNameDeclarationImpl<LSFWindowDeclaration, WindowStubElement> implements LSFWindowDeclaration {

    protected LSFWindowDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFWindowDeclarationImpl(@NotNull WindowStubElement windowStubElement, @NotNull IStubElementType nodeType) {
        super(windowStubElement, nodeType);
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
        return LSFIcons.WINDOW;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.WINDOW;
    }
}
