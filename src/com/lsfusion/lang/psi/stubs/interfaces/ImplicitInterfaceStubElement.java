package com.lsfusion.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.lsfusion.psi.declarations.LSFImplicitInterfacePropStatement;

import java.util.List;

public interface ImplicitInterfaceStubElement extends StubElement<LSFImplicitInterfacePropStatement> {
    List<String> getParamProperties();
}
