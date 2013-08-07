package com.simpleplugin.psi.declarations;

import com.simpleplugin.psi.stubs.MetaStubElement;

import java.util.List;

public interface LSFMetaDeclaration extends LSFFullNameDeclaration<LSFMetaDeclaration, MetaStubElement> {

    int getParamCount();

    List<String> getMetaCode();

    List<String> getDeclParams();
}
