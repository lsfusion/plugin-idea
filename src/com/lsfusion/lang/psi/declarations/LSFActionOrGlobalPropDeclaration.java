package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.psi.LSFNonEmptyPropertyOptions;
import com.lsfusion.lang.psi.LSFPropertyDeclaration;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import com.lsfusion.lang.psi.stubs.FullNameStubElement;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface LSFActionOrGlobalPropDeclaration<This extends LSFActionOrGlobalPropDeclaration<This,Stub>, Stub extends ActionOrPropStubElement<Stub, This>> extends LSFFullNameDeclaration<This, Stub>, LSFInterfacePropStatement, LSFActionOrPropDeclaration {

    @NotNull
    LSFPropertyDeclaration getPropertyDeclaration();

    @Nullable
    LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions();
    
    LSFExplicitClasses getExplicitParams();

    List<String> resolveParamNames();

    byte getPropType();

    boolean isUnfriendly();

    String getCaption();

    Set<LSFActionOrGlobalPropDeclaration> getDependencies();

    Set<LSFActionOrGlobalPropDeclaration> getDependents();
}
