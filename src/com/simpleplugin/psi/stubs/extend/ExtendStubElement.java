package com.simpleplugin.psi.stubs.extend;

import com.intellij.psi.stubs.StubElement;
import com.simpleplugin.psi.LSFStubElement;
import com.simpleplugin.psi.extend.LSFExtend;
import com.simpleplugin.psi.stubs.GlobalStubElement;

public interface ExtendStubElement<T extends LSFExtend<T, Stub>, Stub extends ExtendStubElement<T, Stub>> extends GlobalStubElement<Stub, T> {
}
