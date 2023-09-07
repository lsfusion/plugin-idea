package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.util.LSFStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.lsfusion.lang.LSFElementGenerator.createExpressionStringValueLiteral;
import static com.lsfusion.util.LSFStringUtils.QUOTE;
import static com.lsfusion.util.LSFStringUtils.quote;

public class LSFExpressionStringValueLiteralImpl extends LSFLocalizedStringValueLiteralImpl implements LSFExpressionStringValueLiteral, PsiLanguageInjectionHost {
    public LSFExpressionStringValueLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull LSFVisitor visitor) {
        visitor.visitExpressionStringValueLiteral(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
        else super.accept(visitor);
    }

    @Override
    public boolean isVariable() {
        return LSFStringUtils.hasSpecialBlock(getText(), true);
    }

    @Override
    public boolean needToBeLocalized() {
        return LSFStringUtils.hasLocalizationBlock(getText(), true);
    }

    protected List<LSFStringUtils.SpecialBlock> getSpecialBlockList(String literal) {
        return LSFStringUtils.specialBlockList(literal, true);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        LSFExpressionStringValueLiteral newLiteral = createExpressionStringValueLiteral(getProject(), quote(text));
        replace(newLiteral);
        return this;
    }

    @Override
    public @NotNull LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new LiteralTextEscaper<PsiLanguageInjectionHost>(LSFExpressionStringValueLiteralImpl.this) {
            // This does not support nested interpolation levels.
            @Override
            public boolean decode(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
                unescapeQuotes(rangeInsideHost, outChars);
                return true;
            }

            @Override
            public int getOffsetInHost(int offsetInDecoded, @NotNull TextRange rangeInsideHost) {
                if (offsetInDecoded < 0 || offsetInDecoded > rangeInsideHost.getLength()) {
                    return -1;
                }

                String blockText = rangeInsideHost.substring(myHost.getText());
                int pos = 0;
                int escapedQuotes = 0;
                
                while (pos < blockText.length()) {
                    boolean isEscapedQuote = false;
                    
                    if (blockText.charAt(pos) == '\\' && pos+1 < blockText.length()) {
                        if (blockText.charAt(pos + 1) == QUOTE) {
                            ++escapedQuotes;
                            isEscapedQuote = true;
                        } else if (pos - escapedQuotes == offsetInDecoded) {
                            break;
                        }
                        ++pos;
                    }
                    
                    if (pos - escapedQuotes == offsetInDecoded) {
                        // When this is the first escaped quote return the start position of the \' sequence
                        if (isEscapedQuote && escapedQuotes == 1) --pos;
                        break;
                    }
                    
                    ++pos;
                }
                
                return rangeInsideHost.getStartOffset() + pos;
            }

            @Override
            public boolean isOneLine() {
                return false;
            }

            private void unescapeQuotes(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
                String substring = rangeInsideHost.substring(myHost.getText());
                outChars.append(LSFStringUtils.unescapeQuotes(substring));
            }
        };
    }

    public PsiElement handleElementRename(@NotNull String newText) throws IncorrectOperationException {
        LSFExpressionStringValueLiteral newLiteral = createExpressionStringValueLiteral(getProject(), newText);
        return replace(newLiteral);
    }
}
