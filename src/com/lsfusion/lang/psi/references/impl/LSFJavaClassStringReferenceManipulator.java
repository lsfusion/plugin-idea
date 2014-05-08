package com.lsfusion.lang.psi.references.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.psi.references.LSFJavaClassStringReference;

public class LSFJavaClassStringReferenceManipulator extends AbstractElementManipulator<LSFJavaClassStringReference> {
    @Override
    public LSFJavaClassStringReference handleContentChange(LSFJavaClassStringReference element, TextRange range, String newContent) throws IncorrectOperationException {
        final String oldText = element.getText();
        String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
        element.setNewText(newText);
        return element;
    }

    @Override
    public TextRange getRangeInElement(LSFJavaClassStringReference element) {
        //убираем кавычки
        return new TextRange(1, Math.max(1, element.getTextLength() - 1));
    }
}
