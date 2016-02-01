package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.cache.ParamClassesCache;
import com.lsfusion.lang.psi.cache.ValueClassCache;
import com.lsfusion.lang.psi.declarations.LSFLocalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Set;

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
    public LSFExClassSet resolveExValueClass(boolean infer) {
        return ValueClassCache.getInstance(getProject()).resolveValueClassWithCaching(this, infer);
    }

    @Override
    public LSFExClassSet resolveExValueClassNoCache(boolean infer) {
        return LSFExClassSet.toEx(LSFPsiImplUtil.resolveClass(getClassName()));
    }

    @Override
    @Nullable
    public List<LSFExClassSet> resolveExParamClasses() {
        return ParamClassesCache.getInstance(getProject()).resolveParamClassesWithCaching(this);
    }

    @Override
    public List<LSFExClassSet> resolveExParamClassesNoCache() {
        return LSFExClassSet.toEx(LSFPsiImplUtil.resolveClasses(getClassNameList()));
    }

    @Override
    @Nullable
    public List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass) {
        return resolveExParamClasses();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.LOCAL_PROPERTY;
    }

    @Override
    public String getPresentableText() {
        return getDeclName() + getParamPresentableText();
    }

    public String getParamPresentableText() {
        List<LSFClassSet> classes = resolveParamClasses();
        return "(" + StringUtils.join(classes, ", ") + ")";
    }

    public List<LSFClassSet> resolveParamClasses() {
        return LSFGlobalPropDeclarationImpl.finishParamClasses(this);
    }

    public LSFClassSet resolveValueClass() {
        return LSFGlobalPropDeclarationImpl.finishValueClass(this);
    }

    @NotNull
    public String getValuePresentableText() {
        LSFClassSet valueClass = resolveValueClass();
        return ": " + (valueClass == null ? "?" : valueClass);
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

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public Set<LSFPropDeclaration> getDependencies() {
        return LSFGlobalPropDeclarationImpl.getPropDependencies(this);
    }

    @Override
    public Set<LSFPropDeclaration> getDependents() {
        return LSFGlobalPropDeclarationImpl.getPropDependents(this);
    }

    @Override
    public Integer getComplexity() {
        return LSFGlobalPropDeclarationImpl.getPropComplexity(this);
    }

    @Override
    public Icon getIcon() {
        return getIcon(0);
    }

    public PsiElement getLookupObject() { // пока не совсем понятно зачем
        return this;
    }
}
