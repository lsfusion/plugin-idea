package com.simpleplugin.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.stubs.interfaces.ImplicitValueStubElement;

public interface LSFImplicitValuePropStatement extends StubBasedPsiElement<ImplicitValueStubElement> {
    LSFPropertyStatement getPropertyStatement();

    LSFImplicitInterfacePropStatement getImplicitInterfacePropertyStatement();
}
