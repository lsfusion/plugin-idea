package com.lsfusion.lang;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Quick-fix that replaces the first occurrence of {@code oldText} inside an element's text with {@code newText}.
 * Used mainly for deprecation warnings of the form "... Use 'newName' instead", where only a single token
 * (e.g. an option name) needs to be swapped while the rest of the statement is preserved.
 */
public class LSFReplaceFix implements IntentionAction {
    private final SmartPsiElementPointer<PsiElement> elementPointer;
    private final String oldText;
    private final String newText;

    public LSFReplaceFix(@NotNull PsiElement element, @NotNull String oldText, @NotNull String newText) {
        this.elementPointer = SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);
        this.oldText = oldText;
        this.newText = newText;
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return "Replace '" + oldText + "' with '" + newText + "'";
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Replace deprecated lsFusion construct";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        PsiElement element = elementPointer.getElement();
        return element != null && element.isValid() && element.getText().contains(oldText);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = elementPointer.getElement();
        if (element == null || !element.isValid()) {
            return;
        }
        int relativeOffset = element.getText().indexOf(oldText);
        if (relativeOffset < 0) {
            return;
        }
        PsiFile containingFile = element.getContainingFile();
        if (!FileModificationService.getInstance().prepareFileForWrite(containingFile)) {
            return;
        }
        Document document = PsiDocumentManager.getInstance(project).getDocument(containingFile);
        if (document == null) {
            return;
        }
        int start = element.getTextRange().getStartOffset() + relativeOffset;
        document.replaceString(start, start + oldText.length(), newText);
        PsiDocumentManager.getInstance(project).commitDocument(document);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
