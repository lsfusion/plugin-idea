package com.simpleplugin.psi.extend;

import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.declarations.LSFStaticObjectDeclaration;
import com.simpleplugin.psi.stubs.extend.ExtendClassStubElement;

import java.util.List;
import java.util.Set;

public interface LSFClassExtend extends LSFExtend<LSFClassExtend, ExtendClassStubElement> {
    
    List<LSFClassDeclaration> resolveExtends();

    List<String> getShortExtends();
    
    List<LSFStaticObjectDeclaration> getStaticObjects();

    Set<LSFStaticObjectDeclaration> resolveStaticObjectDuplicates();
}
