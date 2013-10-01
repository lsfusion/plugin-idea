package com.simpleplugin.psi.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.simpleplugin.psi.LSFAnyTokens;
import com.simpleplugin.psi.stubs.MetaStubElement;

import java.util.List;

public interface LSFMetaDeclaration extends LSFFullNameDeclaration<LSFMetaDeclaration, MetaStubElement> {

    int getParamCount();

    List<Pair<String,IElementType>> getMetaCode();
    PsiElement findOffsetInCode(int offset);

    List<String> getDeclParams();
    
    void setBody(LSFAnyTokens tokens);
}
