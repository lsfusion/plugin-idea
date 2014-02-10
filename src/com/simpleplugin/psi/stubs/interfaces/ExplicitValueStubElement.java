package com.simpleplugin.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.simpleplugin.psi.declarations.LSFExplicitValuePropStatement;

import java.util.List;

public interface ExplicitValueStubElement extends StubElement<LSFExplicitValuePropStatement> {
    List<String> getValueClasses();
}
