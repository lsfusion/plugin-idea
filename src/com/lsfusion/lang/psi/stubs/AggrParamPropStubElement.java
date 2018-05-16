package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFAggrParamGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface AggrParamPropStubElement extends PropStubElement<AggrParamPropStubElement, LSFAggrParamGlobalPropDeclaration> {

    LSFExplicitClasses getParamExplicitClasses();

    Set<String> getParamExplicitValues();
}
