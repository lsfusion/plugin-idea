package com.lsfusion.lang.psi;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;

public interface LSFStubbedElement<This extends LSFStubbedElement<This, Stub>, Stub extends LSFStubElement<Stub, This>> extends LSFElement, StubBasedPsiElement<Stub> {

    boolean isInMetaDecl();
}
