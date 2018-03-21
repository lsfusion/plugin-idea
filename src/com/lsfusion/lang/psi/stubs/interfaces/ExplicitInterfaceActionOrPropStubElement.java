package com.lsfusion.lang.psi.stubs.interfaces;

import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionOrPropStatement;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ExplicitInterfaceActionOrPropStubElement<T extends LSFExplicitInterfaceActionOrPropStatement> extends StubElement<T> {

    @NotNull
    String getDeclName();

    @Nullable
    LSFExplicitClasses getParamExplicitClasses();

    byte getPropType();
}
