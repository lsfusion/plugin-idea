package com.lsfusion.formatter;

import com.intellij.formatting.FormattingContext;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import org.jetbrains.annotations.NotNull;

public class LSFFormattingModelBuilder implements FormattingModelBuilder {
    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        PsiFile psiFile = formattingContext.getPsiElement().getContainingFile();
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
        return FormattingModelProvider.createFormattingModelForPsiFile(psiFile, new LSFPlainBlock(SourceTreeToPsiMap.psiElementToTree(psiFile), new LSFCodeStyle(settings)), settings);
    }

    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return FormattingModelBuilder.super.getRangeAffectingIndent(file, offset, elementAtOffset);
    }
}