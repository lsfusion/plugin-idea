package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceStubElement;

public interface LSFExplicitInterfacePropStatement extends StubBasedPsiElement<ExplicitInterfaceStubElement> {
    LSFPropertyStatement getPropertyStatement();

    LSFExplicitValuePropStatement getExplicitValuePropertyStatement();
}
