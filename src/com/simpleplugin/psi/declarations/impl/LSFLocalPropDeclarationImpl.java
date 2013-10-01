package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
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

import javax.swing.*;
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
    public LSFClassSet resolveValueClass(boolean infer) {
        return LSFPsiImplUtil.resolveClass(getClassName());
    }

    @Override
    @NotNull
    public List<LSFClassSet> resolveParamClasses() {
        return LSFPsiImplUtil.resolveClass(getClassNameList());
    }

    @Override
    @NotNull
    public List<LSFClassSet> inferParamClasses(LSFClassSet valueClass) {
        return resolveParamClasses(); 
    }
    
    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return AllIcons.Nodes.Property;
    }
}
