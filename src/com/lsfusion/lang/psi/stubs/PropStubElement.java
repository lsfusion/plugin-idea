package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.types.PropStubElementType;

public interface PropStubElement<This extends ActionOrPropStubElement<This, Decl>, Decl extends LSFActionOrGlobalPropDeclaration<Decl, This>> extends ActionOrPropStubElement<This, Decl> {
}
