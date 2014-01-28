package com.simpleplugin.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.context.LSFExpression;
import com.simpleplugin.psi.declarations.LSFExplicitInterfacePropStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PsiUtils {

    public static List<LSFExpression> collectExpressions(final PsiFile file, final Editor editor, int offset) {
        Document document = editor.getDocument();
        int textLength = document.getTextLength();
        if (textLength == 0) {
            return Collections.emptyList();
        }

        if (offset >= textLength) {
            offset = textLength - 1;
        } else if (offset < 0) {
            offset = 0;
        }

        CharSequence documentText = document.getCharsSequence();

        if (!isLsfIdentifierPart(documentText.charAt(offset)) && offset > 0 && isLsfIdentifierPart(documentText.charAt(offset - 1))) {
            offset--;
        }

        final PsiElement elementAtCaret = file.findElementAt(offset);
        final List<LSFExpression> expressions = new ArrayList<LSFExpression>();
        final List<TextRange> ranges = new ArrayList<TextRange>();

        LSFExpression expression = PsiTreeUtil.getParentOfType(elementAtCaret, LSFExpression.class);
        while (expression != null) {
            //пропускаем скобочные выражения
            TextRange range = expression.getTextRange();
            if (!ranges.contains(range)) {
                expressions.add(expression);
                ranges.add(range);
            }
            expression = PsiTreeUtil.getParentOfType(expression, LSFExpression.class);
        }
        return expressions;
    }

    public static boolean isLsfIdentifierPart(char ch) {
        return Character.isJavaIdentifierPart(ch);
    }

    public static String getPropertyStatementPresentableText(LSFExplicitInterfacePropStatement propertyStatement) {
        LSFPropertyStatement property = propertyStatement.getPropertyStatement();

        String text = property.getPresentableText();

        String caption = property.getCaption();
        if (caption != null) {
            text += " " + caption;
        }

        LSFClassSet valueClass = property.resolveValueClass(false);
        if (!property.isAction()) {
            text += ": " + valueClass;
        }

        return text;
    }
}
