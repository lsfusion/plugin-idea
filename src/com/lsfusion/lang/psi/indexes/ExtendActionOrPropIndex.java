package com.lsfusion.lang.psi.indexes;

import com.lsfusion.lang.psi.declarations.LSFActionOrPropExtend;

// Общий базовый индекс для extend Action/Property (ActionOrProp уровень)
public abstract class ExtendActionOrPropIndex<T extends LSFActionOrPropExtend<T, ?>> extends LSFStringStubIndex<T> {
}
