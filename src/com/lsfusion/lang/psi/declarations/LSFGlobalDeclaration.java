package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFGlobalElement;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;

public interface LSFGlobalDeclaration<This extends LSFGlobalDeclaration<This,Stub>, Stub extends GlobalStubElement<Stub, This>> extends LSFDeclaration, LSFGlobalElement<This, Stub> {

    String getCanonicalName();
}
