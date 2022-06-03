package com.lsfusion;

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.*;
import org.jetbrains.annotations.NotNull;

public class LSFStatementUpDownMover extends StatementUpDownMover {

    @Override
    public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile file, @NotNull MoveInfo moveInfo, boolean down) {
        //toMove is current selected range. toMove2 is target range (always 1 line). It swaps toMove and toMove2, so we need to check if
        //element in targetRange is inside of meta / form / design. If so, we will swap selected range with whole meta/ form / design
        boolean move = false;
        Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        if (document != null && moveInfo.toMove2 != null) {
            PsiElement sourceElement = findElement(file, document, moveInfo.toMove.startLine);
            if(sourceElement != null) {
                PsiElement parentElement = getSourceElement(sourceElement);
                if (parentElement != sourceElement) {
                    sourceElement = parentElement;
                    int endLine = document.getLineNumber(sourceElement.getTextOffset() + sourceElement.getTextLength()) + 1;
                    moveInfo.toMove = new LineRange(document.getLineNumber(sourceElement.getTextOffset()), endLine);
                    if (down) {
                        moveInfo.toMove2 = new LineRange(endLine, endLine + 1);
                    }
                    move = true;
                }

                if (!isInMeta(sourceElement)) {
                    PsiElement element = findElement(file, document, moveInfo.toMove2.startLine);
                    if (element != null) {
                        PsiElement targetElement = getTargetElement(element, sourceElement);
                        if (targetElement != element) {
                            moveInfo.toMove2 = new LineRange(document.getLineNumber(targetElement.getTextOffset()), document.getLineNumber(targetElement.getTextOffset() + targetElement.getTextLength()) + 1);
                            move = true;
                        }
                    }
                }
            }
        }
        return move;
    }

    private PsiElement findElement(PsiFile file, Document document, int startLine) {
        PsiElement element = null;
        for (int offset = document.getLineStartOffset(startLine); offset < document.getLineEndOffset(startLine); offset++) {
            element = file.findElementAt(offset);
            if (!(element instanceof PsiWhiteSpace)) {
                break;
            }
        }
        return element;
    }

    private PsiElement getSourceElement(PsiElement element) {
        //if source is metacode, FORM or DESIGN header, we will move whole element
        if(element != null) {
            if (isInMetaStatementHeader(element)) {
                while (isInMeta(element)) {
                    element = element.getParent();
                }
            } else if (isInFormHeader(element)) {
                element = PsiTreeUtil.getParentOfType(element, LSFFormStatement.class);
            } else if (isInDesignHeader(element)) {
                element = PsiTreeUtil.getParentOfType(element, LSFDesignStatement.class);
            }
        }
        return element;
    }

    private PsiElement getTargetElement(PsiElement element, PsiElement sourceElement) {
        //if target is inside metacode, FORM or DESIGN, we will move whole element
        if(element != null) {
            while (isInMeta(element)) {
                element = element.getParent();
            }
            PsiElement formOrDesignParent = PsiTreeUtil.getParentOfType(element, LSFFormStatement.class, LSFDesignStatement.class);
            if(formOrDesignParent != null && !PsiTreeUtil.isAncestor(sourceElement, formOrDesignParent, false) && !PsiTreeUtil.isAncestor(formOrDesignParent, sourceElement, false)) {
                element = formOrDesignParent;
            }
        }
        return element;
    }


    public boolean isInMeta(PsiElement element) {
        return LSFReferenceAnnotator.isInMetaUsage(element) || PsiTreeUtil.getParentOfType(element, LSFMetaCodeStatement.class) != null;
    }

    public boolean isInMetaStatementHeader(PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, LSFMetaCodeStatementHeader.class) != null;
    }

    public boolean isInFormHeader(PsiElement element) {
        PsiElement parent = element.getParent();
        return parent instanceof LSFExtendingFormDeclaration || parent instanceof LSFFormDecl;
    }

    public boolean isInDesignHeader(PsiElement element) {
        PsiElement parent = element.getParent();
        return parent instanceof LSFDesignHeader;
    }
}