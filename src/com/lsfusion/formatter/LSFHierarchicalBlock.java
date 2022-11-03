package com.lsfusion.formatter;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.lsfusion.lang.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFHierarchicalBlock extends LSFAbstractBlock {
    BlockType type;

    public LSFHierarchicalBlock(ASTNode node, Indent indent, BlockType type) {
        super(node, indent);
        this.type = type;
    }

    @Override
    protected List<Block> buildChildren() {
        ASTNode child = myNode.getFirstChildNode();
        ArrayList<Block> result = new ArrayList<>();

        while (child != null) {
            if (!containsWhiteSpacesOnly(child)) {
                processChild(result, child, isNoneIndent(child.getPsi()) ? getNoneIndent() : getNormalIndent());
            }
            child = child.getTreeNext();
        }
        return result;
    }

    private boolean isNoneIndent(PsiElement psi) {
        switch (type) {
            case ACTION:
            case DESIGN:
                return isRBrace(psi);
            case NAVIGATOR:
                return isNavigatorBegin(psi) || isRBrace(psi);
            case FORM:
                return isFormBegin(psi) || isFormEnd(psi);
            case CLASS:
                return isClassBegin(psi) || isRBrace(psi);
            case FILTERGROUP:
                return isFilterGroupBegin(psi);
            case META:
                return isMetaBegin(psi) || isMetaEnd(psi);
            case OVERRIDE:
                return isOverrideBegin(psi) || isOverrideEnd(psi);
            default:
                return false;
        }
    }

    private boolean isFormBegin(PsiElement element) {
        return element instanceof LSFFormDecl || element instanceof LSFExtendingFormDeclaration;
    }

    private boolean isFormEnd(PsiElement element) {
        return element instanceof LSFEmptyStatement;
    }
    
    private boolean isNavigatorBegin(PsiElement element) {
        return (element instanceof LeafPsiElement && element.getText().equals("NAVIGATOR"));
    }

    private boolean isLBrace(PsiElement element) {
        return element instanceof LeafPsiElement && element.getText().equals("{");
    }

    private boolean isRBrace(PsiElement element) {
        return element instanceof LeafPsiElement && element.getText().equals("}");
    }

    private boolean isClassBegin(PsiElement element) {
        return element instanceof LSFClassDecl || element instanceof LSFExtendingClassDeclaration || isLBrace(element);
    }

    private boolean isFilterGroupBegin(PsiElement element) {
        return (element instanceof LeafPsiElement && element.getText().equals("FILTERGROUP")) || element.getText().equals("EXTEND");
    }

    private boolean isMetaBegin(PsiElement element) {
        return (element instanceof LeafPsiElement && element.getText().equals("META")) || element instanceof LSFMetaCodeStatementHeader;
    }

    private boolean isMetaEnd(PsiElement element) {
        return (element instanceof LeafPsiElement && element.getText().equals("END")) || element instanceof LSFMetaCodeBodyRightBrace;
    }

    private boolean isOverrideBegin(PsiElement element) {
        return element instanceof LSFMappedPropertyClassParamDeclare;
    }

    private boolean isOverrideEnd(PsiElement element) {
        return element instanceof LeafPsiElement && element.getText().equals(";");
    }

    @Override
    protected @Nullable Indent getChildIndent() {
        return getNormalIndent();
    }
}