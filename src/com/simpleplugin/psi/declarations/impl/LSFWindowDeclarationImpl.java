package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFWindowDeclaration;
import com.simpleplugin.psi.stubs.WindowStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
