package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormOrActionDeclaration;

public interface FormOrActionStubElement<This extends FormOrActionStubElement<This, Decl>, Decl extends LSFFormOrActionDeclaration<Decl, This>> extends FullNameStubElement<This, Decl> {
}
