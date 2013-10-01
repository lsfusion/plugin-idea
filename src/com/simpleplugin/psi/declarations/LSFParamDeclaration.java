package com.simpleplugin.psi.declarations;

import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.meta.MetaTransaction;

public interface LSFParamDeclaration extends LSFExprParamDeclaration {
    
    void ensureClass(LSFValueClass decl, MetaTransaction metaTrans);
}
