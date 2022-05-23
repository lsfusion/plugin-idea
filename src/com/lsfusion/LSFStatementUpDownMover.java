package com.lsfusion;

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.LSFMetaCodeStatement;
import com.lsfusion.lang.psi.LSFMetaCodeStatementHeader;
import org.jetbrains.annotations.NotNull;

public class LSFStatementUpDownMover extends StatementUpDownMover {

    @Override
    public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile file, @NotNull MoveInfo moveInfo, boolean down) {
        //toMove is current selected range. toMove2 is target range (always 1 line). It swaps toMove and toMove2, so we need to check if
        //element in targetRange is inside of meta. If so, we will swap selected range with whole meta
        boolean move = false;
        Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        if (document != null && moveInfo.toMove2 != null) {
            PsiElement sourceElement = file.findElementAt(document.getLineStartOffset(moveInfo.toMove.startLine));

            //if source is metacode header, we will move whole metacode usage
            if(sourceElement != null && isInMetaStatementHeader(sourceElement)) {
                while (isInMeta(sourceElement)) {
                    sourceElement = sourceElement.getParent();
                }
                int endLine = document.getLineNumber(sourceElement.getTextOffset() + sourceElement.getTextLength()) + 1;
                moveInfo.toMove = new LineRange(document.getLineNumber(sourceElement.getTextOffset()), endLine);
                if(down) {
                    moveInfo.toMove2 = new LineRange(endLine, endLine + 1);
                }
                move = true;
            }

            if (!isInMeta(sourceElement)) {
                PsiElement element = file.findElementAt(document.getLineStartOffset(moveInfo.toMove2.startLine));
                if (element != null) {
                    PsiElement targetElement = element;
                    while (isInMeta(targetElement)) {
                        targetElement = targetElement.getParent();
                    }
                    if (targetElement != element) {
                        moveInfo.toMove2 = new LineRange(document.getLineNumber(targetElement.getTextOffset()), document.getLineNumber(targetElement.getTextOffset() + targetElement.getTextLength()) + 1);
                        move = true;
                    }
                }
            }
        }
        return move;
    }

    public static boolean isInMeta(PsiElement element) {
        return LSFReferenceAnnotator.isInMetaUsage(element) || PsiTreeUtil.getParentOfType(element, LSFMetaCodeStatement.class) != null;
    }

    public static boolean isInMetaStatementHeader(PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, LSFMetaCodeStatementHeader.class) != null;
    }
}