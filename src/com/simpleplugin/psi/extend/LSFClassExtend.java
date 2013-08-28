package com.simpleplugin.psi.extend;

import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.stubs.extend.ExtendClassStubElement;

import java.util.List;

public interface LSFClassExtend extends LSFExtend<LSFClassExtend, ExtendClassStubElement> {
    
    List<LSFClassDeclaration> resolveExtends();
}
