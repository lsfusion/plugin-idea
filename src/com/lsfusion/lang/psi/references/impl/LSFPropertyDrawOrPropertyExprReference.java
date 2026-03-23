package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFAliasUsage;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFObjectUsageList;
import com.lsfusion.lang.psi.LSFPropertyDrawOrPropertyExprUsage;
import com.lsfusion.lang.psi.LSFFormPropertyDrawPropertyUsage;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.LSFResolveUtil;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class LSFPropertyDrawOrPropertyExprReference extends LSFPropertyDrawReferenceImpl {

    public LSFPropertyDrawOrPropertyExprReference(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return getPropertyDrawOrPropertyExprUsage() == null ? null : this;
    }

    @Override
    public LSFId resolve() {
        LSFDeclaration declaration = resolveTargetDeclaration();
        return declaration == null ? null : declaration.getNameIdentifier();
    }

    @Override
    public LSFPropertyDrawDeclaration resolveDecl() {
        LSFDeclaration declaration = resolveTargetDeclaration();
        return declaration instanceof LSFPropertyDrawDeclaration ? (LSFPropertyDrawDeclaration) declaration : null;
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        if (getPropertyDrawOrPropertyExprUsage() == null) {
            return new LSFResolveResult(Collections.emptyList());
        }

        LSFResolveResult result = super.resolveNoCache();
        if (!result.declarations.isEmpty() || getAliasUsage() != null) {
            return result;
        }

        LSFDeclaration propertyDeclaration = resolvePropertyDeclaration();
        if (propertyDeclaration == null) {
            return result;
        }
        return new LSFResolveResult(Collections.singletonList(propertyDeclaration));
    }

    @Nullable
    public abstract LSFPropertyDrawOrPropertyExprUsage getPropertyDrawOrPropertyExprUsage();

    @Nullable
    @Override
    public LSFAliasUsage getAliasUsage() {
        LSFPropertyDrawOrPropertyExprUsage usage = getPropertyDrawOrPropertyExprUsage();
        return usage == null ? null : PsiTreeUtil.getChildOfType(usage, LSFAliasUsage.class);
    }

    @Nullable
    @Override
    public LSFFormPropertyDrawPropertyUsage getFormPropertyDrawPropertyUsage() {
        LSFPropertyDrawOrPropertyExprUsage usage = getPropertyDrawOrPropertyExprUsage();
        return usage == null ? null : PsiTreeUtil.getChildOfType(usage, LSFFormPropertyDrawPropertyUsage.class);
    }

    @Nullable
    @Override
    public LSFObjectUsageList getObjectUsageList() {
        LSFPropertyDrawOrPropertyExprUsage usage = getPropertyDrawOrPropertyExprUsage();
        return usage == null ? null : PsiTreeUtil.getChildOfType(usage, LSFObjectUsageList.class);
    }

    @Nullable
    private LSFDeclaration resolveTargetDeclaration() {
        LSFResolveResult result = multiResolveDecl(true);
        return result == null ? null : LSFResolveUtil.singleResolve(result.declarations);
    }

    @Nullable
    private LSFDeclaration resolvePropertyDeclaration() {
        LSFFormPropertyDrawPropertyUsage propertyUsage = getFormPropertyDrawPropertyUsage();
        if (propertyUsage == null) {
            return null;
        }

        List<LSFClassSet> usageClasses = LSFPsiImplUtil.resolveParamClasses(getObjectUsageList());
        return LSFElementGenerator
                .createPropRefFromText(propertyUsage.getSimpleName().getText(), null, getLSFFile(), null, usageClasses, false, false)
                .resolveDecl();
    }
}
