package com.simpleplugin.psi.declarations;

import com.simpleplugin.psi.stubs.MetaStubElement;

public interface LSFMetaDeclaration extends LSFFullNameDeclaration<LSFMetaDeclaration, MetaStubElement> {

    int getParamCount();
}
