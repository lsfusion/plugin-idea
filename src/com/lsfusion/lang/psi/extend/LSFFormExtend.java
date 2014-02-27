package com.lsfusion.lang.psi.extend;

import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;

import java.util.Collection;
import java.util.Set;

public interface LSFFormExtend extends LSFExtend<LSFFormExtend, ExtendFormStubElement> {
    
    Collection<LSFObjectDeclaration> getObjectDecls();

    Collection<LSFGroupObjectDeclaration> getGroupObjectDecls();

    Collection<LSFPropertyDrawDeclaration> getPropertyDrawDecls();

    Set<LSFDeclaration> resolveDuplicates();
}
