package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.lang.psi.stubs.TableStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFTableDeclarationImpl extends LSFFullNameDeclarationImpl<LSFTableDeclaration, TableStubElement> implements LSFTableDeclaration {

    public LSFTableDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFTableDeclarationImpl(@NotNull TableStubElement tableStubElement, @NotNull IStubElementType nodeType) {
        super(tableStubElement, nodeType);
    }

    @Nullable
    protected abstract LSFSimpleName getSimpleName();

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.TABLE;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.TABLE;
    }
}
