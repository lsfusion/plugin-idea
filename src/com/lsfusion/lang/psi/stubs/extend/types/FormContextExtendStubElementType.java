package com.lsfusion.lang.psi.stubs.extend.types;

import com.lsfusion.lang.psi.extend.LSFFormContextExtend;
import com.lsfusion.lang.psi.stubs.extend.FormContextExtendStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class FormContextExtendStubElementType<T extends LSFFormContextExtend<T, Stub>, Stub extends FormContextExtendStubElement<T, Stub>> extends ExtendStubElementType<T, Stub> {

    protected FormContextExtendStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }
}
