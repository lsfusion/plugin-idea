package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LSFFormElementDeclaration extends LSFDeclaration {
    
    @NotNull
    LSFFormExtend getFormExtend();
      
    @Nullable
    LSFFormDeclaration resolveFormDecl();
}
