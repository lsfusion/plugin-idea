package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.LSFGlobalElement;
import com.lsfusion.lang.psi.LSFStubElement;

public interface GlobalStubElement<This extends GlobalStubElement<This, Decl>, Decl extends LSFGlobalElement<Decl, This>> extends LSFStubElement<This, Decl> {

    String getGlobalName();
}
