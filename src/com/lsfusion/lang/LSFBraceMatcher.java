package com.lsfusion.lang;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.psi.LSFTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFBraceMatcher implements PairedBraceMatcher {

    private final BracePair[] pairs = new BracePair[] {
            new BracePair(LSFTypes.META, LSFTypes.END, true),
            new BracePair(LSFTypes.LBRACE, LSFTypes.RBRACE, true),
            new BracePair(LSFTypes.LBRAC, LSFTypes.RBRAC, false),
            new BracePair(LSFTypes.LSQBR, LSFTypes.RSQBR, false)
    };

    public BracePair[] getPairs() {
        return pairs;
    }

    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
