package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFElement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.LSFStubbedElement;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitInterfaceStubElement;
import org.jetbrains.annotations.NotNull;

public interface LSFImplicitInterfacePropStatement extends LSFStubbedElement<LSFImplicitInterfacePropStatement, ImplicitInterfaceStubElement> {
    @NotNull
    LSFPropertyStatement getPropertyStatement();
}
