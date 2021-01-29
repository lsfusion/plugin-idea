package com.lsfusion.lang.psi;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;

public interface LSFGlobalElement<This extends LSFGlobalElement<This, Stub>, Stub extends GlobalStubElement<Stub, This>> extends LSFStubbedElement<This, Stub> {
    
    String getGlobalName();
}
