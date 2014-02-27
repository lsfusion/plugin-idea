package com.lsfusion.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.psi.LSFPropertyStatement;
import com.lsfusion.psi.stubs.interfaces.ImplicitInterfaceStubElement;

public interface LSFImplicitInterfacePropStatement extends StubBasedPsiElement<ImplicitInterfaceStubElement> {
    LSFPropertyStatement getPropertyStatement();
}
