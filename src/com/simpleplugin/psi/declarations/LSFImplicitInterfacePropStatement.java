package com.simpleplugin.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.stubs.interfaces.ImplicitInterfaceStubElement;

public interface LSFImplicitInterfacePropStatement extends StubBasedPsiElement<ImplicitInterfaceStubElement> {
    LSFPropertyStatement getPropertyStatement();
}
