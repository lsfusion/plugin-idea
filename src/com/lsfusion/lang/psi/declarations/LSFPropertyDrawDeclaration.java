package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFFormPropertyName;
import com.lsfusion.lang.psi.LSFObjectUsageList;
import com.lsfusion.lang.psi.LSFSimpleName;

public interface LSFPropertyDrawDeclaration extends LSFFormElementDeclaration {
    
    LSFFormPropertyName getFormPropertyName();
    
    LSFObjectUsageList getObjectUsageList();

    LSFSimpleName getSimpleName();
}
