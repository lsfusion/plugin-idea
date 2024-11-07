package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.util.CollectionQuery;
import com.lsfusion.lang.LSFResolvingError;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.references.LSFFormElseNoParamsActionReference;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class LSFFormElseNoParamsActionReferenceImpl extends LSFFullNameReferenceImpl<LSFFormOrActionDeclaration, LSFFormOrActionDeclaration> implements LSFFormElseNoParamsActionReference {

    public LSFFormElseNoParamsActionReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected FullNameStubElementType<?, LSFFormDeclaration> getStubElementType() {
        return LSFStubElementTypes.FORM; // по умолчанию ищем свойства
    }

    protected Condition<LSFStatementActionDeclaration> getActionCondition() {
        final List<LSFClassSet> fDirectClasses = new ArrayList<>();
        return decl -> {
            List<LSFClassSet> declClasses = decl.resolveParamClasses();
            return declClasses == null || (declClasses.size() == fDirectClasses.size() && LSFPsiImplUtil.containsAll(declClasses, fDirectClasses, false));
        };
    }

    @Override
    protected Collection<? extends LSFFormOrActionDeclaration> resolveDeclarations() {
        Collection<? extends LSFFormOrActionDeclaration> declarations = BaseUtils.emptyList();

        if (declarations.isEmpty()) {
            declarations = super.resolveDeclarations();
        }

        // ищем действия
        if(declarations.isEmpty()) {
            declarations = LSFFullNameReferenceImpl.findElements(this, Collections.singletonList(LSFStubElementTypes.STATEMENTACTION), getActionCondition(), BaseUtils.immutableCast(getFinalizer()));
        }

        return declarations;
    }

    @Override
    protected Collection<? extends LSFFormOrActionDeclaration> resolveNoConditionDeclarations() {
        Collection<? extends LSFFormOrActionDeclaration> declarations = BaseUtils.emptyList();

        if(declarations.isEmpty())
            declarations = new CollectionQuery<LSFStatementActionDeclaration>(LSFFullNameReferenceImpl.findNoConditionElements(this, Collections.singletonList(LSFStubElementTypes.STATEMENTACTION), BaseUtils.immutableCast(getFinalizer()))).findAll();

        return declarations;
    }

    @Override
    public LSFResolvingError resolveNotFoundErrorAnnotation(Collection<? extends LSFDeclaration> similarDeclarations, boolean canBeDeclaredAfterAndNotChecked) {
        String errorText;
        boolean noSuchProperty = similarDeclarations.isEmpty();
        if (similarDeclarations.size() != 1) {
            if (noSuchProperty) {
                errorText = "Form or action without params '" + getNameRef() + "' not found";
            } else {
                errorText = "Cannot resolve form or action without params " + getNameRef();
            }
        } else {
            errorText = similarDeclarations.iterator().next().getPresentableText() + " cannot be applied to " +
                    getNameRef() + "()";
        }

        return new LSFResolvingError(this, errorText, !noSuchProperty);
    }
}
