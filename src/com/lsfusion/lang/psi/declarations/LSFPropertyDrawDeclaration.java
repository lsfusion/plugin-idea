package com.lsfusion.psi.declarations;

import com.lsfusion.psi.LSFFormPropertyName;
import com.lsfusion.psi.LSFObjectUsageList;
import com.lsfusion.psi.LSFSimpleName;

public interface LSFPropertyDrawDeclaration extends LSFFormElementDeclaration {
    
    LSFFormPropertyName getFormPropertyName();
    
    LSFObjectUsageList getObjectUsageList();

    LSFSimpleName getSimpleName();
}
