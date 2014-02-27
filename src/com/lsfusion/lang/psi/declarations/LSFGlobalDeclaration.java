package com.lsfusion.psi.declarations;

import com.lsfusion.psi.LSFGlobalElement;
import com.lsfusion.psi.stubs.GlobalStubElement;

public interface LSFGlobalDeclaration<This extends LSFGlobalDeclaration<This,Stub>, Stub extends GlobalStubElement<Stub, This>> extends LSFDeclaration, LSFGlobalElement<This, Stub> {
}
