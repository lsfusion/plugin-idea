package com.simpleplugin.psi.declarations;

import com.simpleplugin.psi.LSFFormPropertyName;
import com.simpleplugin.psi.LSFObjectUsageList;
import com.simpleplugin.psi.LSFSimpleName;

public interface LSFPropertyDrawDeclaration extends LSFFormElementDeclaration {
    
    LSFFormPropertyName getFormPropertyName();
    
    LSFObjectUsageList getObjectUsageList();

    LSFSimpleName getSimpleName();
}
