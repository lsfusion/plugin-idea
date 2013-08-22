package com.simpleplugin.psi.declarations;

import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.stubs.MetaStubElement;

import java.util.List;

public interface LSFMetaDeclaration extends LSFFullNameDeclaration<LSFMetaDeclaration, MetaStubElement> {

    int getParamCount();

    List<String> getMetaCode();
    PsiElement findOffsetInCode(int offset);

    List<String> getDeclParams();
}
