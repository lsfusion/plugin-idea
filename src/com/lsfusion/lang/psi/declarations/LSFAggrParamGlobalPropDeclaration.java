package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.stubs.AggrParamPropStubElement;

import java.util.Set;

public interface LSFAggrParamGlobalPropDeclaration extends LSFGlobalPropDeclaration<LSFAggrParamGlobalPropDeclaration, AggrParamPropStubElement>, LSFExplicitInterfaceProp<AggrParamPropStubElement>, LSFExplicitValueProp<AggrParamPropStubElement> {

    Set<String> getExplicitValues();

    default byte getPropType() {
        return 1;
    }
}
