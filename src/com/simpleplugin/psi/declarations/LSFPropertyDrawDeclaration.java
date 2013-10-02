package com.simpleplugin.psi.declarations;

import com.simpleplugin.psi.LSFFormPropertyName;
import com.simpleplugin.psi.LSFObjectUsageList;
import com.simpleplugin.psi.references.LSFPropReference;

public interface LSFPropertyDrawDeclaration extends LSFFormElementDeclaration {
    
    LSFFormPropertyName getFormPropertyName();
    
    LSFObjectUsageList getObjectUsageList();
    
}
