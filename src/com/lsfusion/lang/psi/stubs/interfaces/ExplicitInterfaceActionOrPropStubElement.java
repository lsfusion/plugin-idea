package com.lsfusion.lang.psi.stubs.interfaces;

import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFStubElement;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionOrPropStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ExplicitInterfaceActionOrPropStubElement<This extends ExplicitInterfaceActionOrPropStubElement<This, Decl>, Decl extends LSFExplicitInterfaceActionOrPropStatement<Decl, This>> extends LSFStubElement<This, Decl> {

    @NotNull
    String getDeclName();

    @Nullable
    LSFExplicitClasses getParamExplicitClasses();

    byte getPropType();
}
