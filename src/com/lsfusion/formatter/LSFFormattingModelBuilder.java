package com.lsfusion.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.xml.XmlFormattingModel;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormattingDocumentModelImpl;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;

public class LSFFormattingModelBuilder implements FormattingModelBuilder {

    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        PsiFile psiFile = element.getContainingFile();
        return new XmlFormattingModel(psiFile, new LSFSimpleBlock(SourceTreeToPsiMap.psiElementToTree(psiFile)), FormattingDocumentModelImpl.createOn(psiFile));
    }

    //unavailable for <idea-version since-build="202.6397.94"/>, use since 203.5981.155
/*    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        return FormattingModelBuilder.super.createModel(formattingContext);
    }*/

    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return FormattingModelBuilder.super.getRangeAffectingIndent(file, offset, elementAtOffset);
    }
}