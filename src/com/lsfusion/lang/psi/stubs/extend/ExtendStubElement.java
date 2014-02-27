package com.lsfusion.psi.stubs.extend;

import com.lsfusion.psi.extend.LSFExtend;
import com.lsfusion.psi.stubs.GlobalStubElement;

public interface ExtendStubElement<T extends LSFExtend<T, Stub>, Stub extends ExtendStubElement<T, Stub>> extends GlobalStubElement<Stub, T> {
}
