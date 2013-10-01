package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.simpleplugin.LSFParserDefinition;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.stubs.MetaStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class LSFMetaDeclarationImpl extends LSFFullNameDeclarationImpl<LSFMetaDeclaration, MetaStubElement> implements LSFMetaDeclaration {

    public LSFMetaDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFMetaDeclarationImpl(@NotNull MetaStubElement metaStubElement, @NotNull IStubElementType nodeType) {
        super(metaStubElement, nodeType);
    }

    public abstract LSFSimpleName getSimpleName();

    public abstract LSFMetaDeclIdList getMetaDeclIdList();

    public abstract LSFAnyTokens getAnyTokens();

    @Override
    public boolean isCorrect() {
        return super.isCorrect() && getAnyTokens()!=null;
    }

    @Override
    public int getParamCount() {
        MetaStubElement stub = getStub();
        if(stub != null)
            return stub.getParamCount();

        return getDeclParams().size();
    }

    private void recReadMetaWhiteSpaceOrComments(ASTNode node, boolean prev, List<Pair<String, IElementType>> tokens) {
        if(LSFParserDefinition.isWhiteSpaceOrComment(node.getElementType())) {
            if(prev) {
                recReadMetaWhiteSpaceOrComments(node.getTreePrev(), prev, tokens);
                tokens.add(new Pair<String, IElementType>(node.getText(), node.getElementType()));
            } else {
                tokens.add(new Pair<String, IElementType>(node.getText(), node.getElementType()));
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
        return getAnyTokens().findElementAt(offset);        
    }

    @Override
    public List<Pair<String,IElementType>> getMetaCode() {
        List<Pair<String, IElementType>> tokens = new ArrayList<Pair<String, IElementType>>();
        ASTNode node = getAnyTokens().getNode();

        readMetaWhiteSpaceOrComments(node, true, tokens);
        for(ASTNode anyToken : node.getChildren(null))
            tokens.add(new Pair<String, IElementType>(anyToken.getText(), anyToken.getElementType()));
        readMetaWhiteSpaceOrComments(node, false, tokens);
        return tokens;
    }

    @Override
    public List<String> getDeclParams() {
        List<String> result = new ArrayList<String>();
        LSFMetaDeclIdList metaDeclIdList = getMetaDeclIdList();
        if(metaDeclIdList != null) {
            for(LSFMetaDeclId decl : metaDeclIdList.getMetaDeclIdList())
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
        return AllIcons.Nodes.AbstractMethod;
    }

    @Override
    public String getPresentableText() {
        return getDeclName() + "(" + getMetaDeclIdList().getText() + ")";
    }
}
