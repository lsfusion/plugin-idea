package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitInterfaceStubElement;

public interface LSFImplicitInterfacePropStatement extends StubBasedPsiElement<ImplicitInterfaceStubElement> {
    LSFPropertyStatement getPropertyStatement();
}
