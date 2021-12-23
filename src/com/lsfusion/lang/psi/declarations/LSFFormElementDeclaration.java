package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.declarations.impl.LSFFormExtendElement;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LSFFormElementDeclaration<T extends LSFFormElementDeclaration<T>> extends LSFFormExtendElement<T> {
    
    @NotNull
    LSFFormExtend getFormExtend();
      
    @Nullable
    LSFFormDeclaration resolveFormDecl();
}
