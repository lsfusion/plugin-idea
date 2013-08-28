package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.LSFBuiltInClassName;
import com.simpleplugin.psi.LSFClassName;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFObjectDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFObjectDeclarationImpl extends LSFExprParamDeclarationImpl implements LSFObjectDeclaration {

    public LSFObjectDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    protected abstract LSFSimpleName getSimpleName();

    @NotNull
    protected abstract LSFClassName getClassName();

    @Nullable
    public LSFClassSet resolveClass() {
        return LSFPsiImplUtil.resolveClass(getClassName());
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        LSFSimpleName simpleName = getSimpleName();
        if(simpleName!=null)
            return simpleName;

        LSFClassName className = getClassName();
        LSFBuiltInClassName builtInClassName = className.getBuiltInClassName();
        if(builtInClassName!=null)
            return builtInClassName;
        
        return className.getCustomClassUsage().getSimpleName();
    }
}
