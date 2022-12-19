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
            String fileSuffix = fileText.substring(literal.getTextOffset() + literal.getText().length());

            for (LSFStringUtils.SpecialBlock block : blocks) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    filePrefix.append(" + ");
                }
                filePrefix.append("'").append(literal.getText(), lastIndex, block.start).append("'").append(" + ");

                registrar.addPlace(filePrefix.toString(), fileSuffix, literal, new TextRange(block.start + 2, block.end));
                filePrefix.append("STRING(").append(literal.getText(), block.start + 2, block.end).append(")");
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
