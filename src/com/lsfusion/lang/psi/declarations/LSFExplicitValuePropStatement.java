package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitValueStubElement;
import org.jetbrains.annotations.NotNull;

public interface LSFExplicitValuePropStatement extends StubBasedPsiElement<ExplicitValueStubElement> {
    @NotNull
    LSFPropertyStatement getPropertyStatement();

    @NotNull
    LSFImplicitValuePropStatement getImplicitValuePropertyStatement();
}
