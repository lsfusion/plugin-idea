package com.lsfusion.formatter;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFNonEmptyImportPropertyUsageListWithIds;
import com.lsfusion.lang.psi.LSFNonEmptyModuleUsageList;
import com.lsfusion.lang.psi.LSFNonEmptyPropertyOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFPlainBlock extends LSFAbstractBlock {

    public LSFPlainBlock(ASTNode node) {
        this(node, getNoneIndent(), BlockType.SIMPLE);
    }

    public LSFPlainBlock(ASTNode node, Indent indent, BlockType type) {
        super(node, indent, type);
    }

    @Override
    protected List<Block> buildChildren() {
        ArrayList<Block> result = new ArrayList<>();
        ASTNode child = myNode.getFirstChildNode();
        while (child != null) {
            if (!containsWhiteSpacesOnly(child)) {
                processChild(result, child, getNoneIndent());
            }
            child = child.getTreeNext();
        }
        return result;
    }

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block block, @NotNull Block block1) {
        if (type == BlockType.HEADER && ((LSFPlainBlock) block1).type != BlockType.COMMENT) {
            return LINE_SPACING;
        }
        return super.getSpacing(block, block1);
    }


    @Override
    protected @Nullable Indent getChildIndent() {
        return indent;
    }
}