package com.lsfusion.lang.psi.stubs.extend;

import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;

public interface ExtendStubElement<T extends LSFExtend<T, Stub>, Stub extends ExtendStubElement<T, Stub>> extends GlobalStubElement<Stub, T> {
}
