package com.lsfusion.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.psi.LSFPropertyStatement;
import com.lsfusion.psi.stubs.interfaces.ExplicitInterfaceStubElement;

public interface LSFExplicitInterfacePropStatement extends StubBasedPsiElement<ExplicitInterfaceStubElement> {
    LSFPropertyStatement getPropertyStatement();

    LSFExplicitValuePropStatement getExplicitValuePropertyStatement();
}
