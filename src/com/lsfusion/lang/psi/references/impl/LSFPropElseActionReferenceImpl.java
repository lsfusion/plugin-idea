package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.util.CollectionQuery;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.Finalizer;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFNoContextActionOrPropertyUsage;
import com.lsfusion.lang.psi.LSFNoContextPropertyUsage;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.references.LSFPropElseActionReference;
import com.lsfusion.lang.psi.stubs.ActionStubElement;
import com.lsfusion.lang.psi.stubs.types.ActionStubElementType;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class LSFPropElseActionReferenceImpl extends LSFActionOrPropReferenceImpl<LSFActionOrPropDeclaration, LSFActionOrGlobalPropDeclaration> implements LSFPropElseActionReference {

    public LSFPropElseActionReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Collection<FullNameStubElementType> getStubElementTypes() {
        return Arrays.asList(LSFStubElementTypes.STATEMENTPROP, LSFStubElementTypes.AGGRPARAMPROP); // по умолчанию ищем свойства
    }

    @Override
    protected FullNameStubElementType getStubElementType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isImplement() {
        return false;
    }

    @Override
    protected Collection<? extends LSFActionOrPropDeclaration> resolveDeclarations() {
        Collection<? extends LSFActionOrPropDeclaration> declarations = BaseUtils.emptyList();

        if (declarations.isEmpty()) {
            declarations = super.resolveDeclarations();
        }

        if(declarations.isEmpty()) {
            if (canBeUsedInDirect()) {
                declarations = LSFFullNameReferenceImpl.findElements(this, BaseUtils.<Condition<LSFGlobalPropDeclaration>>immutableCast(getInDirectCondition()), Finalizer.EMPTY);
            }
        }

        // ищем действия
        if(declarations.isEmpty()) {
            declarations = LSFFullNameReferenceImpl.findElements(this, Collections.singletonList(LSFStubElementTypes.ACTION), BaseUtils.<Condition<LSFActionDeclaration>>immutableCast(getDirectCondition()), BaseUtils.immutableCast(getFinalizer()));
        }

        if(declarations.isEmpty()) {
            if (canBeUsedInDirect()) {
                declarations = LSFFullNameReferenceImpl.findElements(this, Collections.singletonList(LSFStubElementTypes.ACTION), BaseUtils.<Condition<LSFActionDeclaration>>immutableCast(getInDirectCondition()), Finalizer.EMPTY);
            }
        }

        return declarations;
    }

    @Override
    protected Collection<? extends LSFActionOrPropDeclaration> resolveNoConditionDeclarations() {
        Collection<? extends LSFActionOrPropDeclaration> declarations = BaseUtils.emptyList();

        final List<LSFClassSet> usageClasses = getUsageContext();
        if (usageClasses != null) {
            Finalizer<LSFActionOrPropDeclaration> noConditionFinalizer = getNoConditionFinalizer(usageClasses);
            if(declarations.isEmpty())
                declarations = new CollectionQuery<LSFGlobalPropDeclaration>(LSFFullNameReferenceImpl.findNoConditionElements(this, BaseUtils.immutableCast(noConditionFinalizer))).findAll();

            if(declarations.isEmpty())
                declarations = new CollectionQuery<LSFActionDeclaration>(LSFFullNameReferenceImpl.findNoConditionElements(this, BaseUtils.immutableCast(noConditionFinalizer))).findAll();
        }
        return declarations;
    }

    @Override
    public boolean isNoContext(PropertyUsageContext usageContext) {
        return usageContext instanceof LSFNoContextActionOrPropertyUsage;
    }

    @Override
    protected String getErrorName() {
        return "property or action";
    }

    @Override
    public PsiElement getWrapper() {
        throw new UnsupportedOperationException();
    }
}
