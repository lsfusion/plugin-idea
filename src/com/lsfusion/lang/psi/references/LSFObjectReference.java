package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.Nullable;

public interface LSFObjectReference extends LSFFormElementReference<LSFObjectDeclaration> {

    @Nullable
    LSFClassSet resolveClass();
}
