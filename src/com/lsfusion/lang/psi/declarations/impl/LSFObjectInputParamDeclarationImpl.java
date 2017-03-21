package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFObjectInputProps;
import com.lsfusion.lang.psi.LSFObjectUsage;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFObjectInputParamDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFObjectInputParamDeclarationImpl extends LSFParamDeclarationImpl implements LSFObjectInputParamDeclaration {

    public LSFObjectInputParamDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public abstract LSFObjectUsage getObjectUsage();

    @Nullable
    public abstract LSFObjectInputProps getObjectInputProps();

    @Override
    protected LSFSimpleName getSimpleName() {
        LSFObjectInputProps objectInputProps = getObjectInputProps();
        if(objectInputProps != null) {
            LSFSimpleName explicitName = objectInputProps.getSimpleName();
            if (explicitName != null)
                return explicitName;
            return getObjectUsage().getSimpleName();
        }
        assert false;
        return null;
    }
}
