package com.lsfusion.lang.psi.stubs.extend;

import com.lsfusion.lang.psi.declarations.LSFActionOrPropExtend;

// Общий stub интерфейс для extend-элементов действий/свойств (аналог ActionOrProp для деклараций)
public interface ExtendActionOrPropStubElement<This extends LSFActionOrPropExtend<This, Stub>, Stub extends ExtendActionOrPropStubElement<This, Stub>>
        extends ExtendStubElement<This, Stub> {
}
