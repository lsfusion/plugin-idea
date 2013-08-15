package com.simpleplugin.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.simpleplugin.psi.stubs.GlobalStubElement;

public interface LSFGlobalDeclaration<This extends LSFGlobalDeclaration<This,Stub>, Stub extends GlobalStubElement<Stub, This>> extends LSFDeclaration, StubBasedPsiElement<Stub> {
}
