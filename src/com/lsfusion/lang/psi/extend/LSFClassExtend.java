package com.lsfusion.lang.psi.extend;

import com.lsfusion.lang.psi.LSFStringClassRef;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;
import com.lsfusion.lang.psi.stubs.extend.ExtendClassStubElement;

import java.util.List;
import java.util.Set;

public interface LSFClassExtend extends LSFExtend<LSFClassExtend, ExtendClassStubElement> {
    
    List<LSFClassDeclaration> resolveExtends();

    LSFStringClassRef getThis();

    List<LSFStringClassRef> getExtends();
    
    List<LSFStaticObjectDeclaration> getStaticObjects();

    Set<LSFStaticObjectDeclaration> resolveStaticObjectDuplicates();
}
