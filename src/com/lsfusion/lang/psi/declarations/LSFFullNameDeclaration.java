package com.lsfusion.psi.declarations;

import com.lsfusion.psi.stubs.FullNameStubElement;

public interface LSFFullNameDeclaration<This extends LSFFullNameDeclaration<This,Stub>, Stub extends FullNameStubElement<Stub, This>> extends LSFGlobalDeclaration<This, Stub> {

    String getNamespaceName();
}
