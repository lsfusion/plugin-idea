package com.lsfusion.lang.psi.declarations;

import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.stubs.extend.ExtendStubElement;

// Общий промежуточный интерфейс для extend-элементов действия/свойства
public interface LSFActionOrPropExtend<This extends LSFActionOrPropExtend<This, Stub>, Stub extends ExtendStubElement<This, Stub>>
        extends LSFExtend<This, Stub>, LSFDocumentation {
}
