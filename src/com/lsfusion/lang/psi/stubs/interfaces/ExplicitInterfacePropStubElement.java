package com.lsfusion.lang.psi.stubs.interfaces;

import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ExplicitInterfacePropStubElement extends ExplicitInterfaceActionOrPropStubElement<ExplicitInterfacePropStubElement, LSFExplicitInterfacePropStatement> {

    @Nullable
    Set<String> getParamExplicitValues();

}
