package com.lsfusion.lang.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;

import java.util.List;

public interface ExplicitInterfaceStubElement extends StubElement<LSFExplicitInterfacePropStatement> {
    List<String> getParamClasses();
}
