package com.lsfusion.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.lsfusion.psi.declarations.LSFImplicitValuePropStatement;

import java.util.List;

public interface ImplicitValueStubElement extends StubElement<LSFImplicitValuePropStatement> {
    List<String> getValueProperties();
}
