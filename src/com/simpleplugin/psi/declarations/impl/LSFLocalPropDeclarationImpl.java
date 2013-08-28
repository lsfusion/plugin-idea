package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.LSFClassName;
import com.simpleplugin.psi.LSFClassNameList;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFLocalPropDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class LSFLocalPropDeclarationImpl extends LSFDeclarationImpl implements LSFLocalPropDeclaration {

    public LSFLocalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public abstract LSFSimpleName getSimpleName();
    protected abstract LSFClassName getClassName();
    protected abstract LSFClassNameList getClassNameList();


    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }

    @Override
    public LSFClassSet resolveValueClass() {
        return LSFPsiImplUtil.resolveClass(getClassName());
    }

    @Override
    public List<LSFClassSet> resolveParamClasses() {
        return LSFPsiImplUtil.resolveClass(getClassNameList());
    }
}
