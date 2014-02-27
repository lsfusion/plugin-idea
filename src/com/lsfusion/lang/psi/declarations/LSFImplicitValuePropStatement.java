package com.lsfusion.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.psi.LSFPropertyStatement;
import com.lsfusion.psi.stubs.interfaces.ImplicitValueStubElement;

public interface LSFImplicitValuePropStatement extends StubBasedPsiElement<ImplicitValueStubElement> {
    LSFPropertyStatement getPropertyStatement();

    LSFImplicitInterfacePropStatement getImplicitInterfacePropertyStatement();
}
