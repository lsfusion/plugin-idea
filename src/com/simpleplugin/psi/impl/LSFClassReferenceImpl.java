package com.simpleplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.LSFClassReference;
import org.jetbrains.annotations.NotNull;

public class LSFClassReferenceImpl extends LSFElementReferenceImpl implements LSFClassReference {

    public LSFClassReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
