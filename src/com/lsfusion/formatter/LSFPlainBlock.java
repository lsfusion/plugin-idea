package com.lsfusion.formatter;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFPlainBlock extends LSFAbstractBlock {

    public LSFPlainBlock(ASTNode node, LSFCodeStyle codeStyle) {
        this(node, getNoneIndent(), BlockType.DEFAULT, codeStyle);
    }

    public LSFPlainBlock(ASTNode node, Indent indent, BlockType type, LSFCodeStyle codeStyle) {
        super(node, indent, type, codeStyle);
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
            return codeStyle.betweenModuleHeaderStatements();
        }
        return super.getSpacing(block, block1);
    }


    @Override
    protected @Nullable Indent getChildIndent() {
        return indent;
    }
}