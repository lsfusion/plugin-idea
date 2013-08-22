package com.simpleplugin.psi;

import com.intellij.psi.StubBasedPsiElement;
import com.simpleplugin.psi.stubs.GlobalStubElement;

public interface LSFGlobalElement<This extends LSFGlobalElement<This, Stub>, Stub extends GlobalStubElement<Stub, This>> extends LSFElement, StubBasedPsiElement<Stub> {
    
    String getGlobalName();
}
