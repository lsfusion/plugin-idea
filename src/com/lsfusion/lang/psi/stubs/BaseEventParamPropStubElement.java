package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFBaseEventParamGlobalPropDeclaration;

import java.util.Set;

public interface BaseEventParamPropStubElement extends PropStubElement<BaseEventParamPropStubElement, LSFBaseEventParamGlobalPropDeclaration> {

    LSFExplicitClasses getParamExplicitClasses();

    Set<String> getParamExplicitValues();
}
