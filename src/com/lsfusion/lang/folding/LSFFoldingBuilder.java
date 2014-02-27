package com.lsfusion.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.psi.LSFTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFFoldingBuilder implements FoldingBuilder {
    
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        List<FoldingDescriptor> list = new ArrayList<FoldingDescriptor>();
        buildFolding(node, list);
        FoldingDescriptor[] descriptors = new FoldingDescriptor[list.size()];
        return list.toArray(descriptors);
    }

    private static void buildFolding(ASTNode node, List<FoldingDescriptor> list) {
        IElementType elementType = node.getElementType();
        boolean fullFolding = elementType == LSFTypes.META_CODE_BODY
                              || elementType == LSFTypes.NAVIGATOR_ELEMENT_STATEMENT_BODY
                              || elementType == LSFTypes.LIST_ACTION_PROPERTY_DEFINITION_BODY;
        if (fullFolding && node.getTextRange().getLength() > 1) {
            final TextRange range = node.getTextRange();
            list.add(new FoldingDescriptor(node, range));
        }

        if (elementType == LSFTypes.FORM_STATEMENT) {
            ASTNode firstChild = node.getFirstChildNode();
            IElementType firstChildType = firstChild.getElementType();
            if (firstChildType == LSFTypes.FORM_DECL || firstChildType == LSFTypes.EXTENDING_FORM_DECLARATION) {
                final TextRange nodeRange = node.getTextRange();
                TextRange range = new TextRange(nodeRange.getStartOffset() + firstChild.getTextLength(), nodeRange.getEndOffset());
                if (range.getLength() > 2) {
                    list.add(new FoldingDescriptor(node, range));
                }
            }
        }

        for (ASTNode child : node.getChildren(null)) {
            buildFolding(child, list);
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return "{...}";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return node.getElementType() == LSFTypes.META_CODE_BODY;
    }
}
