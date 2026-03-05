package com.lsfusion.formatter;

import com.intellij.formatting.Block;
import com.intellij.formatting.Formatter;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.formatter.WhiteSpaceFormattingStrategy;
import com.intellij.psi.formatter.WhiteSpaceFormattingStrategyFactory;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ActionExpression;
import com.lsfusion.lang.psi.context.LSFExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.intellij.psi.TokenType.BAD_CHARACTER;

public abstract class LSFAbstractBlock extends AbstractBlock {

    protected enum BlockType {
        CONTINUATION, NORMAL, IFACT, IFPROP, HASBEGIN, HASBEGINEND, CLASS, NEPELIST,
        HEADER, LINEFEEDED, COMMENT, SPACEAROUND, SPACEAFTER, DEFAULT;

        public boolean isPlain() {
            return this == HEADER || this == LINEFEEDED || this == COMMENT
                    || this == SPACEAROUND || this == SPACEAFTER || this == DEFAULT;
        }
    }

    protected Indent indent;
    protected BlockType type;

    protected LSFAbstractBlock(ASTNode node, Indent indent, BlockType type) {
        super(node, null, null);
        this.indent = indent;
        this.type = type;
    }

    protected void processChild(List<Block> result, ASTNode child, Indent indent) {
        PsiElement psi = child.getPsi();
        if ((psi.getLanguage() == LSFFileType.INSTANCE.getLanguage() || isBadCharacter(psi)) && !containsWhiteSpacesOnly(child)) {
            BlockType childType = getBlockType(psi);
            if (childType.isPlain()) {
                result.add(new LSFPlainBlock(child, indent, childType));
            } else {
                result.add(new LSFHierarchicalBlock(child, indent, childType));
            }
        }
    }

    private boolean isBadCharacter(PsiElement element) {
        return element instanceof LeafPsiElement && ((LeafPsiElement) element).getElementType() == BAD_CHARACTER;
    }

    private BlockType getBlockType(PsiElement element) {
        if(isContinuation(element)) {
            return BlockType.CONTINUATION;
        } else if(isNormal(element)) {
            return BlockType.NORMAL;
        } else if (element instanceof LSFIfActionPropertyDefinitionBody /*ActionExpression*/) {
            return BlockType.IFACT;
        } else if (element instanceof LSFIfElsePropertyDefinition /*Expression*/) {
            return BlockType.IFPROP;
        } else if (isHasBegin(element)) {
            return BlockType.HASBEGIN;
        } else if (isHasBeginEnd(element)) {
            return BlockType.HASBEGINEND;
        } else if (element instanceof LSFClassStatement) {
            return BlockType.CLASS;
        } else if (element instanceof LSFNonEmptyPropertyExpressionList) {
            return BlockType.NEPELIST;
        }else if (element instanceof LSFModuleHeader) {
            return BlockType.HEADER;
        } else if (isLineFeeded(element)) {
            return BlockType.LINEFEEDED;
        } else if (element instanceof PsiComment) {
            return BlockType.COMMENT;
        } else if(isSpaceAround(element)) {
            return BlockType.SPACEAROUND;
        } else if(isSpaceAfter(element)) {
            return BlockType.SPACEAFTER;
        } else {
            return BlockType.DEFAULT;
        }
    }

    private boolean isContinuation(PsiElement element) {
        return element instanceof LSFNonEmptyModuleUsageList || element instanceof LSFNonEmptyImportPropertyUsageListWithIds;
    }

    private boolean isNormal(PsiElement element) {
        return element instanceof LSFNonEmptyPropertyOptions || element instanceof LSFPropertyCalcStatement;
    }

    private boolean isHasBegin(PsiElement element) {
        return element instanceof LSFFormFilterGroupDeclaration || element instanceof LSFFormExtendFilterGroupDeclaration
                || element instanceof LSFFormFiltersList
                || element instanceof LSFFormEventsList
                || element instanceof LSFFormMappedPropertiesList || element instanceof LSFFormPropertiesNamesDeclList
                || element instanceof LSFExpression
                || element instanceof LSFWriteWhenStatement
                || element instanceof LSFConstraintStatement
                || element instanceof LSFDoMainBody
                || element instanceof LSFForActionPropertyMainBody
                || element instanceof LSFContextActions
                || element instanceof LSFEventStatement
                || element instanceof LSFFormActionObjectList
                || element instanceof LSFOverridePropertyStatement
                || isActionExpressionWithoutEnd(element);
    }

    private boolean isActionExpressionWithoutEnd(PsiElement element) {
        return element instanceof LSFExportDataActionPropertyDefinitionBody
                || element instanceof LSFCaseActionPropertyDefinitionBody;
    }

    private boolean isHasBeginEnd(PsiElement element) {
        return element instanceof ActionExpression
                || element instanceof LSFFormStatement
                || element instanceof LSFComponentBlockStatement
                || element instanceof LSFNavigatorStatement || element instanceof LSFNavigatorElementStatementBody
                || element instanceof LSFMetaCodeDeclarationStatement || element instanceof LSFMetaCodeStatement || element instanceof LSFMetaCodeBody;
    }

    private boolean isLineFeeded(PsiElement element) {
        //OBJECTS in FORM / TREE declaration, not first element
        if(element instanceof LSFFormGroupObjectsList || element instanceof LSFFormTreeGroupObjectList) {
            PsiElement prev = element;
            do {
                prev = prev.getPrevSibling();
            } while (prev instanceof PsiWhiteSpace);
            return !(prev instanceof LSFFormDecl || prev instanceof LSFExtendingFormDeclaration);
        }
        return false;
    }

    private static List<IElementType> spaceAroundTypes = Arrays.asList(
            LSFTypes.EQUALS, LSFTypes.LESS, LSFTypes.GREATER, LSFTypes.LESS_EQUALS, LSFTypes.GREATER_EQUALS,
            LSFTypes.PLUS, LSFTypes.PLUSEQ, LSFTypes.EQ_OPERAND, LSFTypes.ARROW);

    private boolean isSpaceAround(PsiElement element) {
        if(element instanceof LSFTypeMult) {
            return true;
        } else if(element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if(spaceAroundTypes.contains(type)) {
                return true;
            } else if((type == LSFTypes.PLUS || type == LSFTypes.MINUS)) {
                PsiElement parent = element.getParent();
                return parent instanceof LSFAdditivePE;
            }
        }
        return false;
    }

    private boolean isSpaceAfter(PsiElement element) {
        if(element instanceof LeafPsiElement) {
            return ((LeafPsiElement) element).getElementType() == LSFTypes.COMMA;
        }
        return false;
    }

    protected static Indent getContinuationIndent() {
        return Indent.getContinuationIndent();
    }

    protected static Indent getNormalIndent() {
        return Indent.getNormalIndent();
    }

    protected static Indent getNormalChildIndent() {
        return Formatter.getInstance().getNormalIndent(true);
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

    //empty line before element
    protected static Spacing LINE_SPACING = Spacing.createSpacing(0, 1, 2, true, 1);

    //single space before/after element
    protected static Spacing SPACE_SPACING = Spacing.createSpacing(1, 1, 0, true, 1);

    //no space before/after element
    protected static Spacing NOSPACE_SPACING = Spacing.createSpacing(0, 0, 0, true, 1);

    private boolean isSpaceAround(Block block) {
        return block instanceof LSFPlainBlock && ((LSFPlainBlock) block).type == BlockType.SPACEAROUND;
    }

    private boolean isSpaceAfter(Block block) {
        return block instanceof LSFPlainBlock && ((LSFPlainBlock) block).type == BlockType.SPACEAFTER;
    }

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block block, @NotNull Block block1) {
        if(isSpaceAfter(block) || isSpaceAround(block) || isSpaceAround(block1)) {
            return SPACE_SPACING;
        } else if(isSpaceAfter(block1)) {
            return NOSPACE_SPACING;
        } else {
            return DEFAULT_SPACING;
        }
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