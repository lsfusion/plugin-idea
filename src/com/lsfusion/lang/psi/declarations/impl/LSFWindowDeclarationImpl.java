package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.psi.LSFId;
import com.lsfusion.psi.LSFSimpleName;
import com.lsfusion.psi.declarations.LSFWindowDeclaration;
import com.lsfusion.psi.stubs.WindowStubElement;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
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
