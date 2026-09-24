package com.lsfusion.inspections;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFScriptStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Lets a single lsFusion warning be silenced in place, the way it is done in every other language:
//   //noinspection LSFTypeMismatch      above the statement
//   //file:noinspection LSFDeprecated70 at the top of the module
// Without it the only way out is to switch the whole inspection off in the profile.
public class LSFInspectionSuppressor implements InspectionSuppressor {
    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        if (!(element.getContainingFile() instanceof LSFFile)) {
            return false;
        }
        return isSuppressedByComment(element, toolId) || isSuppressedInFile(element.getContainingFile(), toolId);
    }

    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        if (element == null || !(element.getContainingFile() instanceof LSFFile)) {
            return SuppressQuickFix.EMPTY_ARRAY;
        }
        List<SuppressQuickFix> fixes = new ArrayList<>();
        // the inlined body of a META usage is regenerated, a comment written there would not survive
        if (!LSFReferenceAnnotator.isInMetaUsage(element) && getStatement(element) != null) {
            fixes.add(new SuppressForStatementFix(toolId));
        }
        fixes.add(new SuppressForFileFix(toolId));
        return fixes.toArray(SuppressQuickFix.EMPTY_ARRAY);
    }

    // The comment above the construct, at whatever level it is written: above the statement, or above a statement
    // inside a META declaration body - such a comment is copied into every inlined usage and suppresses there too.
    private static boolean isSuppressedByComment(PsiElement element, String toolId) {
        for (PsiElement current = element; current != null && !(current instanceof PsiFile); current = current.getParent()) {
            // several of them may be stacked, one per inspection
            for (PsiElement previous = PsiTreeUtil.skipWhitespacesBackward(current); previous instanceof PsiComment;
                 previous = PsiTreeUtil.skipWhitespacesBackward(previous)) {
                if (isSuppressionOf(previous, toolId, SuppressionUtil.SUPPRESS_IN_LINE_COMMENT_PATTERN)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSuppressionOf(PsiElement comment, String toolId, Pattern pattern) {
        Matcher matcher = pattern.matcher(comment.getText());
        return matcher.matches() && SuppressionUtil.isInspectionToolIdMentioned(matcher.group(1), toolId);
    }

    // only the comments the module starts with: a "file:" comment further down would be easy to miss when reading
    private static boolean isSuppressedInFile(PsiFile file, String toolId) {
        for (PsiElement comment : findFileSuppressionComments(file)) {
            if (isSuppressionOf(comment, toolId, SuppressionUtil.SUPPRESS_IN_FILE_LINE_COMMENT_PATTERN)) {
                return true;
            }
        }
        return false;
    }

    // they may be stacked here too, one per inspection
    private static List<PsiElement> findFileSuppressionComments(PsiFile file) {
        List<PsiElement> comments = new ArrayList<>();
        for (PsiElement child = file.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof PsiComment) {
                if (SuppressionUtil.SUPPRESS_IN_FILE_LINE_COMMENT_PATTERN.matcher(child.getText()).matches()) {
                    comments.add(child);
                }
            } else if (!(child instanceof PsiWhiteSpace)) {
                break;
            }
        }
        return comments;
    }

    private static @Nullable LSFScriptStatement getStatement(PsiElement element) {
        return PsiTreeUtil.getNonStrictParentOfType(element, LSFScriptStatement.class);
    }

    // the ids of several inspections go into one comment, the way the platform writes them: "//noinspection A, B"
    private static void suppressStatement(Project project, PsiElement parent, PsiElement anchor, String toolId, String indent) {
        for (PsiElement previous = PsiTreeUtil.skipWhitespacesBackward(anchor); previous instanceof PsiComment;
             previous = PsiTreeUtil.skipWhitespacesBackward(previous)) {
            if (SuppressionUtil.SUPPRESS_IN_LINE_COMMENT_PATTERN.matcher(previous.getText()).matches()) {
                SuppressionUtil.replaceSuppressionComment(previous, toolId, false, LSFLanguage.INSTANCE);
                return;
            }
        }
        parent.addBefore(SuppressionUtil.createComment(project, "noinspection " + toolId, LSFLanguage.INSTANCE), anchor);
        parent.addBefore(PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n" + indent), anchor);
    }

    // the comment takes over the line the statement was on, so the statement keeps its own indentation
    private static String getIndent(Project project, PsiElement element) {
        Document document = PsiDocumentManager.getInstance(project).getDocument(element.getContainingFile());
        if (document != null) {
            int offset = element.getTextRange().getStartOffset();
            String indent = document.getText(new TextRange(document.getLineStartOffset(document.getLineNumber(offset)), offset));
            if (indent.isBlank()) {
                return indent;
            }
        }
        return "";
    }

    private abstract static class SuppressFix implements SuppressQuickFix {
        protected final String toolId;

        SuppressFix(String toolId) {
            this.toolId = toolId;
        }

        @Override
        public boolean isSuppressAll() {
            return false;
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement element = descriptor.getPsiElement();
            if (element != null && element.isValid()) {
                suppress(project, element);
            }
        }

        protected abstract void suppress(Project project, PsiElement element);
    }

    private static class SuppressForStatementFix extends SuppressFix {
        SuppressForStatementFix(String toolId) {
            super(toolId);
        }

        @Override
        public @NotNull String getFamilyName() {
            return "Suppress for statement";
        }

        @Override
        public boolean isAvailable(@NotNull Project project, @NotNull PsiElement element) {
            return element.getContainingFile() instanceof LSFFile && !LSFReferenceAnnotator.isInMetaUsage(element) && getStatement(element) != null;
        }

        // not SuppressionUtil.createSuppression: it inserts the comment without a line break of its own, and an
        // lsf comment runs to the end of the line, so the statement would end up inside the comment
        @Override
        protected void suppress(Project project, PsiElement element) {
            LSFScriptStatement statement = getStatement(element);
            PsiElement parent = statement == null ? null : statement.getParent();
            if (parent != null) {
                suppressStatement(project, parent, statement, toolId, getIndent(project, statement));
            }
        }
    }

    private static class SuppressForFileFix extends SuppressFix {
        SuppressForFileFix(String toolId) {
            super(toolId);
        }

        @Override
        public @NotNull String getFamilyName() {
            return "Suppress for module file";
        }

        @Override
        public boolean isAvailable(@NotNull Project project, @NotNull PsiElement element) {
            return element.getContainingFile() instanceof LSFFile;
        }

        @Override
        protected void suppress(Project project, PsiElement element) {
            PsiFile file = element.getContainingFile();
            PsiElement anchor = file.getFirstChild();
            if (anchor == null) {
                return;
            }
            // nothing precedes the first child, so the comment to merge into is looked for among the leading ones
            List<PsiElement> existing = findFileSuppressionComments(file);
            if (!existing.isEmpty()) {
                SuppressionUtil.replaceSuppressionComment(existing.get(0), toolId, false, LSFLanguage.INSTANCE);
                return;
            }
            file.addBefore(SuppressionUtil.createComment(project, SuppressionUtil.FILE_PREFIX + "noinspection " + toolId, LSFLanguage.INSTANCE), anchor);
            file.addBefore(PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n"), anchor);
        }
    }
}
