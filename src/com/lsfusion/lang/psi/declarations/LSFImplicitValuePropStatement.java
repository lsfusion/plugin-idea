package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitValueStubElement;

public interface LSFImplicitValuePropStatement extends StubBasedPsiElement<ImplicitValueStubElement> {
    LSFPropertyStatement getPropertyStatement();

    LSFImplicitInterfacePropStatement getImplicitInterfacePropertyStatement();
}
