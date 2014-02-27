package com.lsfusion.lang.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;

import java.util.List;

public interface ExplicitValueStubElement extends StubElement<LSFExplicitValuePropStatement> {
    List<String> getValueClasses();
}
