package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaTransaction;

public interface LSFParamDeclaration extends LSFExprParamDeclaration {

    void ensureClass(LSFValueClass decl, MetaTransaction metaTrans);

    String getClassName();
}
