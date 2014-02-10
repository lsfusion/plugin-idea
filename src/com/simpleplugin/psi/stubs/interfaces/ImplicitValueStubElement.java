package com.simpleplugin.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.simpleplugin.psi.declarations.LSFImplicitValuePropStatement;

import java.util.List;

public interface ImplicitValueStubElement extends StubElement<LSFImplicitValuePropStatement> {
    List<String> getValueProperties();
}
