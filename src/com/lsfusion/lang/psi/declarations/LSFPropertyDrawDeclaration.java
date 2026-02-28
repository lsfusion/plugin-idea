package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFFormPropertyName;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;
import com.lsfusion.lang.psi.LSFObjectUsageList;
import com.lsfusion.lang.psi.LSFSimpleName;
import org.jetbrains.annotations.Nullable;

public interface LSFPropertyDrawDeclaration extends LSFFormElementDeclaration<LSFPropertyDrawDeclaration> {
    
    @Nullable
    LSFFormPropertyName getFormPropertyName();

    @Nullable
    LSFFormPropertyOptionsList getFormPropertyOptionsList();

    @Nullable
    LSFObjectUsageList getObjectUsageList();

    @Nullable
    LSFSimpleName getSimpleName();
}
