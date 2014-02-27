package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.stubs.FullNameStubElement;

public interface LSFFullNameDeclaration<This extends LSFFullNameDeclaration<This,Stub>, Stub extends FullNameStubElement<Stub, This>> extends LSFGlobalDeclaration<This, Stub> {

    String getNamespaceName();
}
