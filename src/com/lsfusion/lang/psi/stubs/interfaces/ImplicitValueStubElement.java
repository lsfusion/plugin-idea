package com.lsfusion.lang.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.LSFStubElement;
import com.lsfusion.lang.psi.declarations.LSFImplicitValuePropStatement;

import java.util.List;

public interface ImplicitValueStubElement extends LSFStubElement<ImplicitValueStubElement, LSFImplicitValuePropStatement> {
    List<String> getValueProperties();
}
