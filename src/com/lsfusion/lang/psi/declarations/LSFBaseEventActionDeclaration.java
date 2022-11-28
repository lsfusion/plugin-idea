package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFAggrPropertyDefinition;
import com.lsfusion.lang.psi.stubs.BaseEventActionStubElement;

import java.util.Set;

public interface LSFBaseEventActionDeclaration extends LSFActionOrGlobalPropDeclaration<LSFBaseEventActionDeclaration, BaseEventActionStubElement> {

    Set<String> getExplicitValues();

    LSFAggrPropertyDefinition getAggrPropertyDefinition();

    default byte getPropType() {
        return 3; //3 - action
    }
}
