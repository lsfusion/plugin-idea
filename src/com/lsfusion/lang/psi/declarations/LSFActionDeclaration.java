package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.stubs.ActionStubElement;

public interface LSFActionDeclaration<This extends LSFActionDeclaration<This,Stub>, Stub extends ActionStubElement<Stub, This>> extends LSFActionOrGlobalPropDeclaration<This, Stub> {

    default byte getPropType() {
        return 3;
    }
}
