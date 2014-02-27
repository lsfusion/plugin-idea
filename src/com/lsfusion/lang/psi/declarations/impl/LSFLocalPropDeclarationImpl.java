package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFLocalPropDeclaration;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public abstract class LSFLocalPropDeclarationImpl extends LSFDeclarationImpl implements LSFLocalPropDeclaration {

    public LSFLocalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public abstract LSFSimpleName getSimpleName();

    @Nullable
    protected abstract LSFClassName getClassName();

    @Nullable
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
    @Nullable
    public List<LSFClassSet> resolveParamClasses() {
        return LSFResolveCache.getParamClassesInstance().resolveWithCaching(this, LSFParamClassesResolver.INSTANCE, true, false);
    }

    @Override
    public List<LSFClassSet> resolveParamClassesNoCache() {
        return LSFPsiImplUtil.resolveClass(getClassNameList());
    }

    @Override
    @Nullable
    public List<LSFClassSet> inferParamClasses(LSFClassSet valueClass) {
        return resolveParamClasses();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.LOCAL_PROPERTY;
    }

    @Override
    public String getPresentableText() {
        return getDeclName() + getParamsPresentableText();
    }

    private String getParamsPresentableText() {
        List<LSFClassSet> classes = resolveParamClasses();
        return "(" + StringUtils.join(classes, ", ") + ")";
    }

    @Override
    public String getSignaturePresentableText() {
        LSFClassSet valueClass = resolveValueClass(false);
        return getParamsPresentableText() + ": " + (valueClass == null ? "?" : valueClass);
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
