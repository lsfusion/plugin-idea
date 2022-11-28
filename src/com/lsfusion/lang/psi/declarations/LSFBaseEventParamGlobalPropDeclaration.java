package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFAggrPropertyDefinition;
import com.lsfusion.lang.psi.stubs.AggrParamPropStubElement;
import com.lsfusion.lang.psi.stubs.BaseEventParamPropStubElement;

import java.util.Set;

public interface LSFBaseEventParamGlobalPropDeclaration extends LSFGlobalPropDeclaration<LSFBaseEventParamGlobalPropDeclaration, BaseEventParamPropStubElement>, LSFExplicitInterfaceProp<LSFBaseEventParamGlobalPropDeclaration, BaseEventParamPropStubElement>, LSFExplicitValueProp<LSFBaseEventParamGlobalPropDeclaration, BaseEventParamPropStubElement> {

    Set<String> getExplicitValues();

    LSFAggrPropertyDefinition getAggrPropertyDefinition();

    default byte getPropType() {
        return 1;
    }
}
