package com.simpleplugin.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFElementImpl;
import com.simpleplugin.psi.LSFStubBasedPsiElement;
import com.simpleplugin.psi.extend.LSFExtend;
import com.simpleplugin.psi.stubs.extend.ExtendStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFExtendImpl<This extends LSFExtend<This, Stub>, Stub extends ExtendStubElement<This, Stub>> extends LSFStubBasedPsiElement<This, Stub> implements LSFExtend<This, Stub> {

    public LSFExtendImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFExtendImpl(@NotNull ASTNode node) {
        super(node);
    }
}
