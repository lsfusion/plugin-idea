package com.simpleplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.LSFPropReference;
import org.jetbrains.annotations.NotNull;

public class LSFPropReferenceImpl extends LSFElementReferenceImpl implements LSFPropReference {

    public LSFPropReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
