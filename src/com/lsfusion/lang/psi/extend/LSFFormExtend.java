package com.lsfusion.psi.extend;

import com.lsfusion.psi.declarations.LSFDeclaration;
import com.lsfusion.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.psi.stubs.extend.ExtendFormStubElement;

import java.util.Collection;
import java.util.Set;

public interface LSFFormExtend extends LSFExtend<LSFFormExtend, ExtendFormStubElement> {
    
    Collection<LSFObjectDeclaration> getObjectDecls();

    Collection<LSFGroupObjectDeclaration> getGroupObjectDecls();

    Collection<LSFPropertyDrawDeclaration> getPropertyDrawDecls();

    Set<LSFDeclaration> resolveDuplicates();
}
