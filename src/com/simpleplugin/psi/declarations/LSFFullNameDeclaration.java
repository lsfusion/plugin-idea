package com.simpleplugin.psi.declarations;

import com.simpleplugin.psi.stubs.FullNameStubElement;
import com.simpleplugin.psi.stubs.GlobalStubElement;

public interface LSFFullNameDeclaration<This extends LSFFullNameDeclaration<This,Stub>, Stub extends FullNameStubElement<Stub, This>> extends LSFGlobalDeclaration<This, Stub> {
}
