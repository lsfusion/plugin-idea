package com.lsfusion.lang.folding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LSFNamedFoldingDescriptor extends FoldingDescriptor {
    private final String placeholderText;

    public LSFNamedFoldingDescriptor(@NotNull PsiElement e, @NotNull String placeholderText, @NotNull Set<Object> dependencies) {
        super(e.getNode(), e.getTextRange(), null, dependencies, true);
        this.placeholderText = placeholderText;
    }

    @Override
    @NotNull
    public String getPlaceholderText() {
        return placeholderText;
    }
}
