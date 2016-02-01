package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceStubElement;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface LSFExplicitInterfacePropStatement extends StubBasedPsiElement<ExplicitInterfaceStubElement>, LSFInterfacePropStatement {

    LSFExplicitClasses getExplicitParams();

    Set<String> getExplicitValues();

    byte getPropType();

    @NotNull
    LSFPropertyStatement getPropertyStatement();

    LSFExplicitValuePropStatement getExplicitValuePropertyStatement();
}
