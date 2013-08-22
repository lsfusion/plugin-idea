package com.simpleplugin.psi.extend;

import com.simpleplugin.psi.LSFElement;
import com.simpleplugin.psi.declarations.LSFObjectDeclaration;
import com.simpleplugin.psi.stubs.extend.ExtendFormStubElement;

import java.util.Collection;

public interface LSFFormExtend extends LSFExtend<LSFFormExtend, ExtendFormStubElement> {
    
    Collection<LSFObjectDeclaration> getObjectDecls();
}
