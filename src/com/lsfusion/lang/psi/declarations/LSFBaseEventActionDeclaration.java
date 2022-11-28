package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFAggrPropertyDefinition;
import com.lsfusion.lang.psi.stubs.BaseEventActionStubElement;
import com.lsfusion.lang.psi.stubs.StatementActionStubElement;

import java.util.Set;

//�� �������� � LSFAggrParamGlobalPropDeclaration
public interface LSFBaseEventActionDeclaration extends LSFActionDeclaration<LSFBaseEventActionDeclaration, BaseEventActionStubElement> {

    default byte getPropType() {
        return 3; //3 - action
    }
}
