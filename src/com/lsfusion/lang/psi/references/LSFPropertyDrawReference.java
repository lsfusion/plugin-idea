package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.psi.LSFAliasUsage;
import com.lsfusion.lang.psi.LSFFormPropertyDrawPropertyUsage;
import com.lsfusion.lang.psi.LSFObjectUsageList;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import org.jetbrains.annotations.Nullable;

public interface LSFPropertyDrawReference extends LSFFormElementReference<LSFPropertyDrawDeclaration> {

    default LSFAliasUsage getAliasUsage() {
        return null;
    }

    @Nullable
    LSFFormPropertyDrawPropertyUsage getFormPropertyDrawPropertyUsage();

    @Nullable
    default LSFObjectUsageList getObjectUsageList() {
        return null;
    }
}
