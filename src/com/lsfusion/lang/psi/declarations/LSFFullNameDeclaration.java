package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.stubs.FullNameStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;

public interface LSFFullNameDeclaration<This extends LSFFullNameDeclaration<This,Stub>, Stub extends FullNameStubElement<Stub, This>> extends LSFGlobalDeclaration<This, Stub> {

    String getNamespaceName();

    String getCanonicalName(); // каноническое имя "по умолчанию"
    
    int getOffset();

    // Тип extend-элемента, связанного с данной декларацией (для поиска implement/extend-элементов)
    // По умолчанию null, должен быть переопределён в нужных декларациях (class/form/action/property)
    default ExtendStubElementType<?, ?> getExtendElementType() {
        return null;
    }
}
