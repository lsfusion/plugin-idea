package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public abstract class LSFReparsableElement extends LazyParseablePsiElement {
    public LSFReparsableElement(@NotNull IElementType type, CharSequence buffer) {
        super(type, buffer);
    }

    public LSFReparsableElement(ASTNode parsed) {
        super(parsed.getElementType(), null);
        
        assert parsed instanceof TreeElement;

        rawAddChildrenWithoutNotifications((TreeElement)parsed);
    }
    
    
}