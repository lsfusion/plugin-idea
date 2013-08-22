package com.simpleplugin.psi.stubs.extend.types;

import com.simpleplugin.psi.extend.LSFExtend;
import com.simpleplugin.psi.stubs.extend.ExtendStubElement;
import com.simpleplugin.psi.stubs.types.GlobalStubElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class ExtendStubElementType<T extends LSFExtend<T, Stub>, Stub extends ExtendStubElement<T, Stub>> extends GlobalStubElementType<Stub, T> {

    protected ExtendStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }
}
