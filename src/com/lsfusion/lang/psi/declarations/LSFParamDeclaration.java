package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFStringClassRef;

public interface LSFParamDeclaration extends LSFExprParamDeclaration {

    void ensureClass(LSFValueClass decl, MetaTransaction metaTrans);

    LSFStringClassRef getClassName();
}
