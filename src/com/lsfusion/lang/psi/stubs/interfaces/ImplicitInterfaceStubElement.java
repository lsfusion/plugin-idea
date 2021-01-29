package com.lsfusion.lang.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.LSFStubElement;
import com.lsfusion.lang.psi.declarations.LSFImplicitInterfacePropStatement;

import java.util.List;

public interface ImplicitInterfaceStubElement extends LSFStubElement<ImplicitInterfaceStubElement, LSFImplicitInterfacePropStatement> {
    List<String> getParamProperties();
}
