package com.simpleplugin.psi.stubs;

import com.simpleplugin.psi.declarations.LSFMetaDeclaration;

public interface MetaStubElement extends FullNameStubElement<MetaStubElement, LSFMetaDeclaration> {

    int getParamCount();
}
