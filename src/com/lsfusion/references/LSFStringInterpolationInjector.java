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

public class LSFStringInterpolationInjector implements MultiHostInjector {
    // dummy property the interpolation expressions are injected into, so that the fragment parses as a valid module.
    // Intentionally contains NO references to real elements and NO host text (no REQUIRE, no NAMESPACE, no surrounding
    // statement text, not even the literal's own text between the blocks): the host module, require scope and parameter
    // context are resolved against the real host file instead (see LSFFile.getModuleDeclaration /
    // LSFGlobalResolver.getRequireScope / LSFPsiUtils.getContextParams), which keeps resolution against the real module
    // and avoids resolving into the virtual module (issues #75, #79, #80, #82).
    private static final String DUMMY_PROPERTY = "interp";

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        assert context instanceof LSFExpressionStringValueLiteralImpl;
        LSFExpressionStringValueLiteralImpl literal = (LSFExpressionStringValueLiteralImpl) context;
        List<LSFStringUtils.SpecialBlock> blocks = LSFStringUtils.getInterpolationBlockList(literal.getText(), true);

        if (!blocks.isEmpty()) {
            registrar.startInjecting(LSFLanguage.INSTANCE);

            String prefix = "MODULE " + LSFElementGenerator.genName + ";\n" + DUMMY_PROPERTY + "=";
            for (LSFStringUtils.SpecialBlock block : blocks) {
                // wrapping in STRING cast is expected by ElementsContextModifier.resolveParamsInsideStringInterpolations
                registrar.addPlace(prefix + "STRING(", ")", literal, new TextRange(block.start + INTERPOLATION_PREFIX.length(), block.end));
                prefix = " + ";
            }

            registrar.doneInjecting();
        }
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(LSFExpressionStringLiteral.class);
    }
}
