package com.lsfusion.formatter;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFHierarchicalBlock extends LSFAbstractBlock {
    public LSFHierarchicalBlock(ASTNode node, Indent indent, BlockType type) {
        super(node, indent, type);
    }

    @Override
    protected List<Block> buildChildren() {
        ArrayList<Block> result = new ArrayList<>();

        ASTNode[] children = myNode.getChildren(null);
        for (int i = 0; i < children.length; i++) {
            ASTNode child = children[i];
            if (!containsWhiteSpacesOnly(child)) {
                boolean first = i == 0;
                boolean last = i == children.length - 1;
                processChild(result, child, getIndent(child.getPsi(), first, last));
            }
        }
        return result;
    }

    private Indent getIndent(PsiElement psi, boolean first, boolean last) {
        switch (type) {
            case CONTINUATION:
                return getContinuationIndent();
            case HASBEGIN:
                if (first) {
                    return getNoneIndent();
                }
                break;
            case IFACT:
                if ((first || isThenElse(psi))) {
                    return getNoneIndent();
                }
                break;
            case IFPROP:
                if ((first || isThenElse(psi)) && notSameLineAsParentIfProp(psi)) {
                    return getNoneIndent();
                }
                break;
            case HASBEGINEND:
                if (first || last) {
                    return getNoneIndent();
                }
                break;
            case CLASS:
                if (first || isLBrace(psi) || last) {
                    return getNoneIndent();
                }
                break;
            case NEPELIST:
                if(notSameLineAsParentPEList(psi)) {
                    return getNoneIndent();
                }
        }
        return getNormalIndent();
    }

    private boolean notSameLineAsParentPEList(PsiElement element) {
        final Document document = PsiDocumentManager.getInstance(element.getProject()).getDocument(element.getContainingFile());
        return document != null && document.getLineNumber(myNode.getPsi().getParent().getTextOffset()) != document.getLineNumber(myNode.getPsi().getTextOffset());
    }

    private boolean notSameLineAsParentIfProp(PsiElement element) {
        final Document document = PsiDocumentManager.getInstance(element.getProject()).getDocument(element.getContainingFile());
        LSFPropertyStatement propertyStatement = PsiTreeUtil.getParentOfType(myNode.getPsi(), LSFPropertyStatement.class);
        return document != null && propertyStatement != null && document.getLineNumber(propertyStatement.getTextOffset()) != document.getLineNumber(myNode.getPsi().getTextOffset());
    }

    private boolean isLBrace(PsiElement element) {
        return element instanceof LeafPsiElement && ((LeafPsiElement) element).getElementType() == LSFTypes.LBRACE;
    }

    private boolean isThenElse(PsiElement element) {
        return element instanceof LeafPsiElement &&
                (((LeafPsiElement) element).getElementType() == LSFTypes.THEN ||
                        ((LeafPsiElement) element).getElementType() == LSFTypes.ELSE);
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block block, @NotNull Block block1) {
        return ((LSFAbstractBlock) block1).type == BlockType.LINEFEEDED ? LINE_SPACING : super.getSpacing(block, block1);
    }

    @Override
    protected @Nullable Indent getChildIndent() {
        return type == BlockType.HASBEGIN ? getNormalChildIndent() : getNormalIndent();
    }
}