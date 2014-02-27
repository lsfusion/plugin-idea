package com.lsfusion.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.psi.LSFPropertyStatement;
import com.lsfusion.psi.stubs.interfaces.ExplicitValueStubElement;

public interface LSFExplicitValuePropStatement extends StubBasedPsiElement<ExplicitValueStubElement> {
    LSFPropertyStatement getPropertyStatement();

    LSFImplicitValuePropStatement getImplicitValuePropertyStatement();
}
