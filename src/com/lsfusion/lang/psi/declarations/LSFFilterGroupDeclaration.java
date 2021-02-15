package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFSimpleName;
import org.jetbrains.annotations.NotNull;

public interface LSFFilterGroupDeclaration extends LSFFormElementDeclaration<LSFFilterGroupDeclaration> {
    @NotNull
    LSFSimpleName getSimpleName();
}
