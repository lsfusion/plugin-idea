package com.lsfusion.lang;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

public class LSFResolvingError {

    public final PsiElement element;
    public final TextRange range;
    public final String text;
    public final boolean underscored;
    public final boolean deprecated;

    public LSFResolvingError(PsiElement element, String text, boolean underscored) {
        this(element, null, text, underscored, false);
    }

    public LSFResolvingError(PsiElement element, TextRange range, String text, boolean underscored) {
        this(element, range, text, underscored, false);
    }

    public LSFResolvingError(PsiElement element, TextRange range, String text, boolean underscored, boolean deprecated) {
        this.element = element;
        this.range = range;
        this.text = text;
        this.underscored = underscored;
        this.deprecated = deprecated;
    }
}
