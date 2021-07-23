package com.lsfusion.lang.psi.declarations;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.lang.psi.LSFMetaCodeDeclBody;
import com.lsfusion.lang.psi.stubs.MetaStubElement;

import java.util.List;

public interface LSFMetaDeclaration extends LSFFullNameDeclaration<LSFMetaDeclaration, MetaStubElement>, LSFDocumentation {

    int getParamCount();

    List<Pair<String,IElementType>> getMetaCode();
    PsiElement findOffsetInCode(int offset);

    List<String> getDeclParams();
    
    void setBody(LSFMetaCodeDeclBody tokens);
}
