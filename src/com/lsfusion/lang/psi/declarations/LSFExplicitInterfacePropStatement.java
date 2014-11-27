package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceStubElement;
import org.jetbrains.annotations.NotNull;

public interface LSFExplicitInterfacePropStatement extends StubBasedPsiElement<ExplicitInterfaceStubElement> {
    @NotNull
    LSFPropertyStatement getPropertyStatement();

    LSFExplicitValuePropStatement getExplicitValuePropertyStatement();
}
