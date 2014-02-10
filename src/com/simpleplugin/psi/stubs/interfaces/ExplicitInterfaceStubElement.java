package com.simpleplugin.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.simpleplugin.psi.declarations.LSFExplicitInterfacePropStatement;

import java.util.List;

public interface ExplicitInterfaceStubElement extends StubElement<LSFExplicitInterfacePropStatement> {
    List<String> getParamClasses();
}
