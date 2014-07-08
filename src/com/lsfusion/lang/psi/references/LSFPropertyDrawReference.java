package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.psi.LSFAliasUsage;
import com.lsfusion.lang.psi.LSFFormPropertyDrawPropertyUsage;
import com.lsfusion.lang.psi.LSFObjectUsageList;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import org.jetbrains.annotations.Nullable;
import com.lsfusion.lang.meta.MetaTransaction;

public interface LSFPropertyDrawReference extends LSFFormElementReference<LSFPropertyDrawDeclaration> {
    @Nullable
    LSFAliasUsage getAliasUsage();

    @Nullable
    LSFFormPropertyDrawPropertyUsage getFormPropertyDrawPropertyUsage();

    @Nullable
    LSFObjectUsageList getObjectUsageList();
}
