package com.lsfusion.spellchecker;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import com.lsfusion.lang.psi.LSFStringValueLiteral;
import com.lsfusion.lang.psi.LSFTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Enables spell-checking for lsFusion: only string literals (captions, messages, etc.) and comments are checked;
 * everything else (keywords, identifiers) is left to the default strategy (which does not spell-check it).
 * Quotes/escapes are treated as word delimiters by the plain-text tokenizer, so they don't produce false positives.
 */
public class LSFSpellcheckingStrategy extends SpellcheckingStrategy {
    @Override
    public @NotNull Tokenizer getTokenizer(PsiElement element) {
        ASTNode node = element.getNode();
        if (node != null && node.getElementType() == LSFTypes.COMMENTS) {
            return TEXT_TOKENIZER;
        }
        if (element instanceof LSFStringValueLiteral) {
            return TEXT_TOKENIZER;
        }
        return super.getTokenizer(element);
    }
}
