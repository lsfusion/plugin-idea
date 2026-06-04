package com.lsfusion.lang;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
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
 * Replaces the first occurrence of {@code oldText} inside an element's text with {@code newText}.
 * Usable both as an annotator fix ({@link IntentionAction}) and as an inspection fix ({@link LocalQuickFix}),
 * so the same fix can back either reporting mechanism.
 */
public class LSFReplaceFix implements IntentionAction, LocalQuickFix {
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
    public @IntentionName @NotNull String getName() {
        return getText();
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

    // IntentionAction entry point
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        doReplace(project);
    }

    // LocalQuickFix entry point
    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        doReplace(project);
    }

    private void doReplace(@NotNull Project project) {
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
