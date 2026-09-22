package com.lsfusion.lang;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.analysis.ErrorQuickFixProvider;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.lsfusion.lang.psi.LSFFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Alt+Enter on a syntax error offers to insert the token the parser was waiting for. The parser (Grammar-Kit) words
// its errors as "';', ',' or END expected, got 'x'": the tokens before "expected" are what could come next, and for
// the ones a developer usually just forgot the fix types them in at the error.
public class LSFErrorQuickFixProvider implements ErrorQuickFixProvider {
    private static final Set<String> INSERTABLE = Set.of(";", ")", "]", "}", "THEN", "DO", "END");

    @Override
    public void registerErrorQuickFix(@NotNull PsiErrorElement error, @NotNull HighlightInfo.Builder builder) {
        // the inlined code of a META usage is regenerated, editing it changes nothing
        if (!(error.getContainingFile() instanceof LSFFile) || LSFReferenceAnnotator.isInMetaUsage(error)) {
            return;
        }
        for (String token : expectedTokens(error.getErrorDescription())) {
            if (INSERTABLE.contains(token)) {
                builder.registerFix(new InsertTokenFix(error, token), null, null, null, null);
            }
        }
    }

    // "'a', 'b' or C expected, got 'x'" -> [a, b, C]; <rule names> are dropped. Past MAX_VARIANTS_TO_DISPLAY the parser
    // ends the list with " and ..." instead of " or x".
    static List<String> expectedTokens(String description) {
        List<String> tokens = new ArrayList<>();
        int expected = description.indexOf(" expected");
        if (expected < 0) {
            return tokens;
        }
        for (String alternative : description.substring(0, expected).split(", | or | and ")) {
            String token = alternative.trim();
            if (token.startsWith("'") && token.endsWith("'") && token.length() > 2) {
                token = token.substring(1, token.length() - 1);
            }
            if (!token.startsWith("<")) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private static class InsertTokenFix extends BaseIntentionAction {
        private final SmartPsiElementPointer<PsiErrorElement> error;
        private final String token;

        InsertTokenFix(PsiErrorElement error, String token) {
            this.error = SmartPointerManager.createPointer(error);
            this.token = token;
            setText("Insert '" + token + "'");
        }

        @Override
        public @NotNull String getFamilyName() {
            return "Insert missing token";
        }

        @Override
        public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
            return currentError() != null;
        }

        // after an edit the pointer may resolve to a new error at the same place that waits for something else
        private PsiErrorElement currentError() {
            PsiErrorElement element = error.getElement();
            return element != null && expectedTokens(element.getErrorDescription()).contains(token) ? element : null;
        }

        // the error element sits right after the last token the parser accepted, so the missing token goes there;
        // a keyword is padded with spaces where it would otherwise touch its neighbours
        @Override
        public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
            PsiErrorElement element = currentError();
            Document document = element == null ? null : PsiDocumentManager.getInstance(project).getDocument(file);
            if (document == null) {
                return;
            }
            int offset = element.getTextRange().getStartOffset();
            String text = token;
            if (Character.isLetter(token.charAt(0))) {
                CharSequence chars = document.getCharsSequence();
                if (offset > 0 && !Character.isWhitespace(chars.charAt(offset - 1))) {
                    text = " " + text;
                }
                if (offset < chars.length() && Character.isJavaIdentifierPart(chars.charAt(offset))) {
                    text = text + " ";
                }
            }
            document.insertString(offset, text);
        }
    }
}
