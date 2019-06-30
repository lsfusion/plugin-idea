package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CollectionQuery;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.lang.psi.references.LSFActionReference;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class LSFActionReferenceImpl extends LSFActionOrPropReferenceImpl<LSFActionDeclaration, LSFActionDeclaration> implements LSFActionReference {

    public LSFActionReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected FullNameStubElementType<?, LSFActionDeclaration> getStubElementType() {
        return LSFStubElementTypes.ACTION;
    }

    public boolean isNoContext(PropertyUsageContext usageContext) {
        return usageContext instanceof LSFNoContextActionUsage || usageContext instanceof LSFNoContextActionOrPropertyUsage;
    }

    public boolean isImplement() {
        PropertyUsageContext usageContext = getPropertyUsageContext();
        if(usageContext instanceof LSFMappedActionClassParamDeclare)
            return usageContext.getParent() instanceof LSFOverrideActionStatement;
        return false;
    }

    @Override
    protected Collection<? extends LSFActionDeclaration> resolveDeclarations() {
        Collection<? extends LSFActionDeclaration> declarations = BaseUtils.emptyList();

        if (declarations.isEmpty()) {
            declarations = super.resolveDeclarations();
        }

        if(canBeUsedInDirect()) {
            if(declarations.isEmpty())
                declarations = LSFFullNameReferenceImpl.findElements(this, BaseUtils.<Condition<LSFActionDeclaration>>immutableCast(getInDirectCondition()), Finalizer.EMPTY);
        }

        return declarations;
    }

    @Override
    protected Collection<? extends LSFActionDeclaration> resolveNoConditionDeclarations() {
        Collection<? extends LSFActionDeclaration> declarations = BaseUtils.emptyList();

        final List<LSFClassSet> usageClasses = getUsageContext();
        if (usageClasses != null) {
            Finalizer<LSFActionDeclaration> noConditionFinalizer = getNoConditionFinalizer(usageClasses);
            if(declarations.isEmpty())
                declarations = new CollectionQuery<LSFActionDeclaration>(LSFFullNameReferenceImpl.findNoConditionElements(this, BaseUtils.immutableCast(noConditionFinalizer))).findAll();
        }
        return declarations;
    }

    @Override
    protected String getErrorName() {
        return "action";
    }

    @Override
    public PsiElement getWrapper() {
        return PsiTreeUtil.getParentOfType(this, LSFActionUsageWrapper.class);
    }
}
