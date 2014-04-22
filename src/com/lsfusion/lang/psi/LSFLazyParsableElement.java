package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public abstract class LSFLazyParsableElement extends LazyParseablePsiElement {
    public LSFLazyParsableElement(@NotNull IElementType type, CharSequence buffer) {
        super(type, buffer);
    }

    public LSFLazyParsableElement(ASTNode parsed) {
        super(parsed.getElementType(), null);
        
        assert parsed instanceof TreeElement;

        rawAddChildrenWithoutNotifications((TreeElement)parsed);
    }
    
    
}