package com.lsfusion.debug;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.lsfusion.lang.LSFFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFDebuggerEditorsProvider extends XDebuggerEditorsProvider {
    @NotNull
    @Override
    public FileType getFileType() {
        return LSFFileType.INSTANCE;
    }

    @NotNull
    @Override
    public Document createDocument(@NotNull final Project project,
                                   @NotNull String text,
                                   @Nullable final XSourcePosition sourcePosition,
                                   @NotNull EvaluationMode mode) {
        //todo:
//        text = text.trim();
//        final PyExpressionCodeFragmentImpl fragment = new PyExpressionCodeFragmentImpl(project, "fragment.py", text, true);
//
//        // Bind to context
//        final PsiElement element = getContextElement(project, sourcePosition);
//        fragment.setContext(element);
//
//        return PsiDocumentManager.getInstance(project).getDocument(fragment);
        return null;
    }

    @Nullable
    private static PsiElement getContextElement(final Project project, XSourcePosition sourcePosition) {
        //todo:
//        if (sourcePosition != null) {
//            final Document document = FileDocumentManager.getInstance().getDocument(sourcePosition.getFile());
//            final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
//            if (psiFile != null) {
//                int offset = sourcePosition.getOffset();
//                if (offset >= 0 && offset < document.getTextLength()) {
//                    final int lineEndOffset = document.getLineEndOffset(document.getLineNumber(offset));
//                    do {
//                        PsiElement element = psiFile.findElementAt(offset);
//                        if (element != null && !(element instanceof PsiWhiteSpace || element instanceof PsiComment)) {
//                            return PyPsiUtils.getStatement(element);
//                        }
//                        offset = element.getTextRange().getEndOffset() + 1;
//                    }
//                    while (offset < lineEndOffset);
//                }
//            }
//        }
        return null;
    }
}
