package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;

public interface MetaStubElement extends FullNameStubElement<MetaStubElement, LSFMetaDeclaration> {

    int getParamCount();
}
