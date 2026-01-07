package com.lsfusion.lang.psi.stubs.extend.types;

import com.lsfusion.lang.psi.declarations.LSFActionOrPropExtend;
import com.lsfusion.lang.psi.stubs.extend.ExtendActionOrPropStubElement;

// Общий тип стаба для extend-элементов действий/свойств (ActionOrProp уровень)
public abstract class ExtendActionOrPropStubElementType<T extends LSFActionOrPropExtend<T, Stub>, Stub extends ExtendActionOrPropStubElement<T, Stub>>
        extends ExtendStubElementType<T, Stub> {

    protected ExtendActionOrPropStubElementType(String debugName) {
        super(debugName);
    }
}
