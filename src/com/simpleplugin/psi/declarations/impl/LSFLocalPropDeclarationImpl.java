package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFLocalPropDeclaration;
import org.apache.commons.lang.StringUtils;
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
        return LSFResolveCache.getValueClassInstance().resolveWithCaching(this, infer ? LSFValueClassResolver.INFER_INSTANCE : LSFValueClassResolver.NO_INFER_INSTANCE, true, false);
    }

    @Override
    public LSFClassSet resolveValueClassNoCache(boolean infer) {
        return LSFPsiImplUtil.resolveClass(getClassName());
    }

    @Override
    @NotNull
    public List<LSFClassSet> resolveParamClasses() {
        return LSFResolveCache.getParamClassesInstance().resolveWithCaching(this, LSFParamClassesResolver.INSTANCE, true, false);
    }

    @Override
    public List<LSFClassSet> resolveParamClassesNoCache() {
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
        return LSFIcons.PROPERTY;
    }

    @Override
    public String getPresentableText() {
        List<LSFClassSet> classes = resolveParamClasses();
        return getDeclName() + "(" + StringUtils.join(classes, ", ") + ")";
    }

    @Override
    public boolean resolveDuplicates() {
        return resolveLocalDuplicates(this);
    }

    private boolean resolveLocalDuplicates(PsiElement current) {
        if (current instanceof LSFListAction) {
            LSFListAction action = (LSFListAction) current;
            for (PsiElement child : action.getChildren()) {
                if (child instanceof LSFLocalPropDeclaration && !(this.equals(child))) {
                    LSFLocalPropDeclaration local = (LSFLocalPropDeclaration) child;
                    if (getDeclName().equals(local.getDeclName()) && LSFGlobalPropDeclarationImpl.resolveEquals(resolveParamClasses(), local.resolveParamClasses())) {
                        return true;
                    }
                }
            }
        }

        PsiElement parent = current.getParent();

        return !(parent == null || parent instanceof LSFFile) && resolveLocalDuplicates(parent);
    }
}
