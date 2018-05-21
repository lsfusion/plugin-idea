package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFLocalPropDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
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
    public LSFClassName getClassName() {
        LSFLocalDataPropertyDefinition def = PsiTreeUtil.getParentOfType(this, LSFLocalDataPropertyDefinition.class, false);
        if (def != null) {
            return def.getClassName();
        }
        return null;
    }

    @Nullable
    public LSFClassNameList getClassNameList() {
        LSFLocalDataPropertyDefinition def = PsiTreeUtil.getParentOfType(this, LSFLocalDataPropertyDefinition.class, false);
        if (def != null) {
            return def.getClassNameList();
        }
        return null;
    }


    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }

    @Override
    public LSFExClassSet resolveExValueClassNoCache(boolean infer) {
        return LSFExClassSet.toEx(LSFPsiImplUtil.resolveClass(getClassName()));
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

    @Override
    public boolean isNoParams() {
        return resolveParamClasses().isEmpty();
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
                    if (getDeclName().equals(local.getDeclName()) && LSFActionOrGlobalPropDeclarationImpl.resolveEquals(resolveParamClasses(), local.resolveParamClasses())) {
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
    public Icon getIcon() {
        return getIcon(0);
    }

    public PsiElement getLookupObject() { // пока не совсем понятно зачем
        return this;
    }

    @Override
    public boolean isAction() {
        return false;
    }
}
