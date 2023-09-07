package com.lsfusion.references;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFExpressionStringLiteral;
import com.lsfusion.lang.psi.LSFExpressionStringValueLiteralImpl;
import com.lsfusion.util.LSFStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static com.lsfusion.util.LSFStringUtils.INTERPOLATION_PREFIX;
import static com.lsfusion.util.LSFStringUtils.QUOTE;

public class LSFStringInterpolationInjector implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        assert context instanceof LSFExpressionStringValueLiteralImpl;
        LSFExpressionStringValueLiteralImpl literal = (LSFExpressionStringValueLiteralImpl) context;
        List<LSFStringUtils.SpecialBlock> blocks = LSFStringUtils.getInterpolationBlockList(literal.getText(), true);

        if (!blocks.isEmpty()) {
            registrar.startInjecting(LSFLanguage.INSTANCE);

            StringBuilder filePrefix = new StringBuilder();
            String fileText = literal.getContainingFile().getText();
            filePrefix.append(fileText, 0, literal.getTextOffset());

            int lastIndex = 1; // do not take starting quote
            boolean isFirst = true;
            String fileSuffix = ")" + fileText.substring(literal.getTextOffset() + literal.getText().length());

            for (LSFStringUtils.SpecialBlock block : blocks) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    filePrefix.append(" + ");
                }
                 filePrefix.append(QUOTE).append(literal.getText(), lastIndex, block.start).append(QUOTE);
                // wrapping in STRING cast is expected by ElementsContextModifier.resolveParamsInsideStringInterpolations
                filePrefix.append(" + STRING(");

                registrar.addPlace(filePrefix.toString(), fileSuffix, literal, new TextRange(block.start + INTERPOLATION_PREFIX.length(), block.end));
                String blockText = literal.getText().substring(block.start + INTERPOLATION_PREFIX.length(), block.end);
                // Removes possible escaping of quotes. This does not support nested interpolation levels.
                filePrefix.append(LSFStringUtils.unescapeQuotes(blockText)).append(")");
                lastIndex = block.end + 1;
            }
            registrar.doneInjecting();
        }
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(LSFExpressionStringLiteral.class);
    }
}
