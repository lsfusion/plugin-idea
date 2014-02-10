package com.simpleplugin.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.stubs.interfaces.ExplicitInterfaceStubElement;

public interface LSFExplicitInterfacePropStatement extends StubBasedPsiElement<ExplicitInterfaceStubElement> {
    LSFPropertyStatement getPropertyStatement();

    LSFExplicitValuePropStatement getExplicitValuePropertyStatement();
}
