package com.lsfusion.formatter;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFSimpleBlock extends LSFAbstractBlock {
    private boolean isLeaf;

    public LSFSimpleBlock(ASTNode node) {
        this(node, getNoneIndent(), false);
    }

    public LSFSimpleBlock(ASTNode node, Indent indent, boolean isLeaf) {
        super(node, indent);
        this.isLeaf = isLeaf;
    }

    @Override
    protected List<Block> buildChildren() {
        ArrayList<Block> result = new ArrayList<>();
        if (!isLeaf) {
            ASTNode child = myNode.getFirstChildNode();
            while (child != null) {
                if (!containsWhiteSpacesOnly(child)) {
                    processChild(result, child, getNoneIndent());
                }
                child = child.getTreeNext();
            }
        }
        return result;
    }

    @Override
    protected @Nullable Indent getChildIndent() {
        return indent;
    }
}