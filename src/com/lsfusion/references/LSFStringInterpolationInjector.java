package com.lsfusion.references;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.LSFElementGenerator;
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
    // dummy property the interpolation expression is injected into, so that the fragment parses as a valid module.
    // Intentionally contains NO references to real elements (no REQUIRE, no NAMESPACE, no surrounding statement text):
    // the host module, require scope and parameter context are resolved against the real host file instead (see
    // LSFFile.getModuleDeclaration / LSFGlobalResolver.getRequireScope / LSFPsiUtils.getContextParams), which keeps
    // resolution against the real module and avoids resolving into the virtual module (issues #75, #79, #80, #82).
    private static final String DUMMY_PROPERTY = "interp";

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        assert context instanceof LSFExpressionStringValueLiteralImpl;
        LSFExpressionStringValueLiteralImpl literal = (LSFExpressionStringValueLiteralImpl) context;
        List<LSFStringUtils.SpecialBlock> blocks = LSFStringUtils.getInterpolationBlockList(literal.getText(), true);

        if (!blocks.isEmpty()) {
            StringBuilder currentPrefix = new StringBuilder(buildFragmentPrefix());
            int lastOffset = 1; // do not take starting quote

            registrar.startInjecting(LSFLanguage.INSTANCE);

            for (LSFStringUtils.SpecialBlock block : blocks) {
                if (block != blocks.get(0)) {
                    currentPrefix.setLength(0);
                    currentPrefix.append(" + ");
                }
                currentPrefix.append(QUOTE).append(literal.getText(), lastOffset, block.start).append(QUOTE);
                // wrapping in STRING cast is expected by ElementsContextModifier.resolveParamsInsideStringInterpolations
                currentPrefix.append(" + STRING(");

                registrar.addPlace(currentPrefix.toString(), ")", literal, new TextRange(block.start + INTERPOLATION_PREFIX.length(), block.end));
                lastOffset = block.end + 1;
            }

            registrar.doneInjecting();
        }
    }

    private String buildFragmentPrefix() {
        return "MODULE " + LSFElementGenerator.genName + ";\n" + DUMMY_PROPERTY + "=";
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(LSFExpressionStringLiteral.class);
    }
}
