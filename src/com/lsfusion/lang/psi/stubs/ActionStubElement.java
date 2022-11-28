package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;

//по аналогии с PropStubElement
public interface ActionStubElement<This extends ActionOrPropStubElement<This, Decl>, Decl extends LSFActionOrGlobalPropDeclaration<Decl, This>> extends ActionOrPropStubElement<This, Decl> {
}
