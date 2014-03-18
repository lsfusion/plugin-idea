package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.psi.LSFMultiCompoundID;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;

public interface LSFComponentReference extends LSFReference<LSFDeclaration> {
    LSFMultiCompoundID getMultiCompoundID();
}
