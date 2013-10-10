package com.simpleplugin.psi.extend;

import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.declarations.LSFGroupObjectDeclaration;
import com.simpleplugin.psi.declarations.LSFObjectDeclaration;
import com.simpleplugin.psi.declarations.LSFPropertyDrawDeclaration;
import com.simpleplugin.psi.stubs.extend.ExtendFormStubElement;

import java.util.Collection;
import java.util.Set;

public interface LSFFormExtend extends LSFExtend<LSFFormExtend, ExtendFormStubElement> {
    
    Collection<LSFObjectDeclaration> getObjectDecls();

    Collection<LSFGroupObjectDeclaration> getGroupObjectDecls();

    Collection<LSFPropertyDrawDeclaration> getPropertyDrawDecls();

    Set<LSFDeclaration> resolveDuplicates();
}
