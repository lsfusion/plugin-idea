package com.lsfusion.references;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFExpressionStringLiteral;
import com.lsfusion.lang.psi.LSFExpressionStringValueLiteralImpl;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFScriptStatement;
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
            StringBuilder currentPrefix = new StringBuilder(buildModulePrefix(literal));
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

    private String buildModulePrefix(PsiElement literal) {
        String moduleHeader = createModuleHeaderString(literal);
        String fileText = literal.getContainingFile().getText();
        String statementBeforeLiteralText = fileText.substring(getOuterStatementOffset(literal), literal.getTextOffset());
        return moduleHeader + statementBeforeLiteralText;
    }
    
    private String createModuleHeaderString(PsiElement literal) {
        String dummyModuleName = LSFElementGenerator.genName;
        return String.format("MODULE %s;\nREQUIRE %s;\nNAMESPACE %s;\n", dummyModuleName, getModuleName(literal), getNamespaceName(literal));
    }
    
    private int getOuterStatementOffset(PsiElement literal) {
        PsiElement outerStatement = PsiTreeUtil.getParentOfType(literal, LSFScriptStatement.class);
        assert outerStatement != null;
        return outerStatement.getTextOffset();
    }
    
    private String getModuleName(PsiElement literal) {
        LSFFile file = (LSFFile) literal.getContainingFile();
        return file.getModuleDeclaration().getDeclName();
    }

    private String getNamespaceName(PsiElement literal) {
        LSFFile file = (LSFFile) literal.getContainingFile();
        return file.getModuleDeclaration().getNamespace();
    }
    
    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(LSFExpressionStringLiteral.class);
    }
}
