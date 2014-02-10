package com.simpleplugin.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.stubs.interfaces.ExplicitValueStubElement;

public interface LSFExplicitValuePropStatement extends StubBasedPsiElement<ExplicitValueStubElement> {
    LSFPropertyStatement getPropertyStatement();

    LSFImplicitValuePropStatement getImplicitValuePropertyStatement();
}
