package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFGroupUsage;
import com.lsfusion.lang.psi.stubs.GroupStubElement;
import org.jetbrains.annotations.Nullable;

public interface LSFGroupDeclaration extends LSFFullNameDeclaration<LSFGroupDeclaration, GroupStubElement> {
    String getGroupName();

    String getCaption();

    @Nullable
    LSFGroupUsage getGroupUsage();
}
