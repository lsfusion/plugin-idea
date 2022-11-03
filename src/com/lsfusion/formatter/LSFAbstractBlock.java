package com.lsfusion.formatter;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.formatter.WhiteSpaceFormattingStrategy;
import com.intellij.psi.formatter.WhiteSpaceFormattingStrategyFactory;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class LSFAbstractBlock extends AbstractBlock {

    protected enum BlockType {
        ACTION, NAVIGATOR, FORM, DESIGN, CLASS, FILTERGROUP, META, OVERRIDE, SIMPLE
    }

    protected Indent indent;

    protected LSFAbstractBlock(ASTNode node, Indent indent) {
        super(node, null, null);
        this.indent = indent;
    }

    protected void processChild(List<Block> result, ASTNode child, Indent indent) {
        PsiElement psi = child.getPsi();
        if (psi.getLanguage() == LSFFileType.INSTANCE.getLanguage() && !containsWhiteSpacesOnly(child)) {
            BlockType type = isHierarchicalBlock(psi);
            if (type == BlockType.SIMPLE) {
                result.add(new LSFSimpleBlock(child, indent, isLeafStatement(psi)));
            } else {
                result.add(new LSFHierarchicalBlock(child, indent, type));
            }
        }
    }

    private BlockType isHierarchicalBlock(PsiElement element) {
        if(element instanceof LSFListActionPropertyDefinitionBody) {
            return BlockType.ACTION;
        } else if(element instanceof LSFNavigatorStatement || element instanceof LSFNavigatorElementStatementBody) {
            return BlockType.NAVIGATOR;
        } else if(element instanceof LSFFormStatement) {
            return BlockType.FORM;
        } else if(element instanceof LSFComponentBlockStatement) {
            return BlockType.DESIGN;
        } else if(element instanceof LSFClassStatement) {
            return BlockType.CLASS;
        } else if(element instanceof LSFFormFilterGroupDeclaration || element instanceof LSFFormExtendFilterGroupDeclaration) {
            return BlockType.FILTERGROUP;
        } else if(element instanceof LSFMetaCodeDeclarationStatement || element instanceof LSFMetaCodeStatement || element instanceof LSFMetaCodeBody) {
            return BlockType.META;
        } else if(element instanceof LSFOverridePropertyStatement) {
            return BlockType.OVERRIDE;
        }else {
            return BlockType.SIMPLE;
        }
    }

    //property
    private boolean isLeafStatement(PsiElement element) {
        return element instanceof LSFExplicitInterfacePropStatement;
    }

    protected static Indent getNormalIndent() {
        return Indent.getNormalIndent();
    }

    protected static Indent getNoneIndent() {
        return Indent.getNoneIndent();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Indent getIndent() {
        return indent;
    }

    private static Spacing DEFAULT_SPACING = Spacing.createSpacing(0, 1, 0, true, 1);

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block block, @NotNull Block block1) {
        return DEFAULT_SPACING;
    }

    protected static boolean containsWhiteSpacesOnly(ASTNode node) {
        if(node.getTextLength() == 0)
            return true;
        PsiElement psiElement = node.getPsi();
        if (psiElement instanceof PsiWhiteSpace) return true;
        WhiteSpaceFormattingStrategy strategy = WhiteSpaceFormattingStrategyFactory.getStrategy(psiElement.getLanguage());
        int length = node.getTextLength();
        return strategy.check(node.getChars(), 0, length) >= length;
    }
}