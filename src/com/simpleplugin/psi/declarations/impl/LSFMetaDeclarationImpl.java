package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.LSFParserDefinition;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.stubs.MetaStubElement;
import org.jetbrains.annotations.NotNull;

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

    private void recReadMetaWhiteSpaceOrComments(ASTNode node, boolean prev, List<String> tokens) {
        if(LSFParserDefinition.isWhiteSpaceOrComment(node.getElementType())) {
            if(prev) {
                recReadMetaWhiteSpaceOrComments(node.getTreePrev(), prev, tokens);
                tokens.add(node.getText());
            } else {
                tokens.add(node.getText());
                recReadMetaWhiteSpaceOrComments(node.getTreeNext(), prev, tokens);
            }
        }
    }

    private void readMetaWhiteSpaceOrComments(ASTNode node, boolean prev, List<String> tokens) {
        recReadMetaWhiteSpaceOrComments(prev ? node.getTreePrev() : node.getTreeNext(), prev, tokens);
    }

    public PsiElement findOffsetInCode(int offset) {
        return getAnyTokens().findElementAt(offset);        
    }

    @Override
    public List<String> getMetaCode() {
        List<String> tokens = new ArrayList<String>();
        ASTNode node = getAnyTokens().getNode();

        readMetaWhiteSpaceOrComments(node, true, tokens);
        for(ASTNode anyToken : node.getChildren(null))
            tokens.add(anyToken.getText());
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
}
