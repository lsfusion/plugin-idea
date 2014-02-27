package com.lsfusion.psi.extend;

import com.lsfusion.psi.declarations.LSFClassDeclaration;
import com.lsfusion.psi.declarations.LSFStaticObjectDeclaration;
import com.lsfusion.psi.stubs.extend.ExtendClassStubElement;

import java.util.List;
import java.util.Set;

public interface LSFClassExtend extends LSFExtend<LSFClassExtend, ExtendClassStubElement> {
    
    List<LSFClassDeclaration> resolveExtends();

    List<String> getShortExtends();
    
    List<LSFStaticObjectDeclaration> getStaticObjects();

    Set<LSFStaticObjectDeclaration> resolveStaticObjectDuplicates();
}
