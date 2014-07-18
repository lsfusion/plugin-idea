package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitValueStubElement;
import org.jetbrains.annotations.NotNull;

public interface LSFImplicitValuePropStatement extends StubBasedPsiElement<ImplicitValueStubElement> {
    @NotNull
    LSFPropertyStatement getPropertyStatement();

    @NotNull
    LSFImplicitInterfacePropStatement getImplicitInterfacePropertyStatement();
}
