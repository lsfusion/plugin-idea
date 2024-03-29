package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;

public interface ActionOrPropStubElement<This extends ActionOrPropStubElement<This, Decl>, Decl extends LSFActionOrGlobalPropDeclaration<Decl, This>> extends FullNameStubElement<This, Decl> {
    
    boolean isNoParams();
}
