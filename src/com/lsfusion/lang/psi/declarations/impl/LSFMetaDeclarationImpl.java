package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFParserDefinition;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.stubs.MetaStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class LSFMetaDeclarationImpl extends LSFFullNameDeclarationImpl<LSFMetaDeclaration, MetaStubElement> implements LSFMetaDeclaration {

    public LSFMetaDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFMetaDeclarationImpl(@NotNull MetaStubElement metaStubElement, @NotNull IStubElementType nodeType) {
        super(metaStubElement, nodeType);
    }

    public abstract LSFSimpleName getSimpleName();

    @Nullable
    public abstract LSFMetaDeclIdList getMetaDeclIdList();

    @Nullable
    public abstract LSFAnyTokens getAnyTokens();

    @Override
    public boolean isCorrect() {
        return super.isCorrect() && getAnyTokens() != null;
    }

    @Override
    public int getParamCount() {
        MetaStubElement stub = getStub();
        if (stub != null)
            return stub.getParamCount();

        return getDeclParams().size();
    }

    private void recReadMetaWhiteSpaceOrComments(ASTNode node, boolean prev, List<Pair<String, IElementType>> tokens) {
        if (node != null && LSFParserDefinition.isWhiteSpaceOrComment(node.getElementType())) {
            if (prev) {
                recReadMetaWhiteSpaceOrComments(node.getTreePrev(), prev, tokens);
                tokens.add(Pair.create(node.getText(), node.getElementType()));
            } else {
                tokens.add(Pair.create(node.getText(), node.getElementType()));
                recReadMetaWhiteSpaceOrComments(node.getTreeNext(), prev, tokens);
            }
        }
    }

    public void setBody(LSFAnyTokens parsed) {
        LSFAnyTokens body = getAnyTokens();
        if (body != null) {
            body.replace(parsed);
        }
    }

    private void readMetaWhiteSpaceOrComments(ASTNode node, boolean prev, List<Pair<String, IElementType>> tokens) {
        recReadMetaWhiteSpaceOrComments(prev ? node.getTreePrev() : node.getTreeNext(), prev, tokens);
    }

    public PsiElement findOffsetInCode(int offset) {
        LSFAnyTokens anyTokens = getAnyTokens();
        return anyTokens == null ? null : anyTokens.findElementAt(offset);
    }

    @Override
    public List<Pair<String, IElementType>> getMetaCode() {
        LSFAnyTokens anyTokens = getAnyTokens();
        if (anyTokens == null) {
            return Collections.emptyList();
        }

        List<Pair<String, IElementType>> tokens = new ArrayList<>();

        ASTNode node = anyTokens.getNode();

        readMetaWhiteSpaceOrComments(node, true, tokens);
        for (ASTNode anyToken : node.getChildren(null))
            tokens.add(Pair.create(anyToken.getText(), anyToken.getElementType()));
        readMetaWhiteSpaceOrComments(node, false, tokens);
        return tokens;
    }

    @Override
    public List<String> getDeclParams() {
        List<String> result = new ArrayList<>();
        LSFMetaDeclIdList metaDeclIdList = getMetaDeclIdList();
        if (metaDeclIdList != null) {
            for (LSFMetaDeclId decl : metaDeclIdList.getMetaDeclIdList())
                result.add(decl.getText());
        }
        return result;
    }

    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.META_DECLARATION;
    }

    @Override
    public String getPresentableText() {
        LSFMetaDeclIdList metaDeclIdList = getMetaDeclIdList();
        return getDeclName() + "(" + (metaDeclIdList == null ? "" : metaDeclIdList.getText()) + ")";
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.META;
    }

    @Override
    protected Condition<LSFMetaDeclaration> getFindDuplicatesCondition() {
        LSFMetaDeclIdList metaDeclIdList = getMetaDeclIdList();
        if (metaDeclIdList == null) {
            return Condition.FALSE;
        }
        final int paramCount = metaDeclIdList.getMetaDeclIdList().size();
        return new Condition<LSFMetaDeclaration>() {
            public boolean value(LSFMetaDeclaration decl) {
                return decl.getParamCount() == paramCount;
            }
        };
    }
}
