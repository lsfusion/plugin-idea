package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFTableDeclaration;
import com.simpleplugin.psi.stubs.TableStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFTableDeclarationImpl extends LSFFullNameDeclarationImpl<LSFTableDeclaration, TableStubElement> implements LSFTableDeclaration {

    public LSFTableDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFTableDeclarationImpl(@NotNull TableStubElement tableStubElement, @NotNull IStubElementType nodeType) {
        super(tableStubElement, nodeType);
    }

    protected abstract LSFSimpleName getSimpleName();

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }
}
