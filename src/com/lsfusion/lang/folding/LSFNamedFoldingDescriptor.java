package com.lsfusion.lang.folding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class LSFNamedFoldingDescriptor extends FoldingDescriptor {
    private final String placeholderText;

    public LSFNamedFoldingDescriptor(@NotNull PsiElement e, @NotNull String placeholderText) {
        this(e, placeholderText, true);
    }

    public LSFNamedFoldingDescriptor(@NotNull PsiElement e, @NotNull String placeholderText, boolean neverExpands) {
        this(e, e.getTextRange(), placeholderText, neverExpands);
    }
    
    public LSFNamedFoldingDescriptor(@NotNull PsiElement e, TextRange range, @NotNull String placeholderText, boolean neverExpands) {
        this(e, range.getStartOffset(), range.getEndOffset(), placeholderText, neverExpands);
    }
    
    public LSFNamedFoldingDescriptor(@NotNull PsiElement e, int start, int end, @NotNull String placeholderText, boolean neverExpands) {
        super(e.getNode(), new TextRange(start, end), null, Collections.emptySet(), neverExpands);
        this.placeholderText = placeholderText;
    }

    @Override
    @NotNull
    public String getPlaceholderText() {
        return placeholderText;
    }
}
