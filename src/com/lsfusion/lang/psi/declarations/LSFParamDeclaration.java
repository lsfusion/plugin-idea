package com.lsfusion.psi.declarations;

import com.lsfusion.classes.LSFValueClass;
import com.lsfusion.meta.MetaTransaction;

public interface LSFParamDeclaration extends LSFExprParamDeclaration {

    void ensureClass(LSFValueClass decl, MetaTransaction metaTrans);

    String getClassName();
}
