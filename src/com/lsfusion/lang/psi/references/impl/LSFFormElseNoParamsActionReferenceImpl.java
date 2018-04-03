package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.util.CollectionQuery;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.Finalizer;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
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


    protected Condition<LSFActionDeclaration> getActionCondition() {
        final List<LSFClassSet> fDirectClasses = new ArrayList<>();
        return new Condition<LSFActionDeclaration>() {
            public boolean value(LSFActionDeclaration decl) {
                List<LSFClassSet> declClasses = decl.resolveParamClasses();
                return declClasses == null || (declClasses.size() == fDirectClasses.size() && LSFPsiImplUtil.containsAll(declClasses, fDirectClasses, false));
            }
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
            declarations = LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), Collections.singletonList(LSFStubElementTypes.ACTION), getLSFFile(), getActionCondition(), BaseUtils.immutableCast(getFinalizer()));
        }

        return declarations;
    }

    @Override
    protected Collection<? extends LSFFormOrActionDeclaration> resolveNoConditionDeclarations() {
        Collection<? extends LSFFormOrActionDeclaration> declarations = BaseUtils.emptyList();

        if(declarations.isEmpty())
            declarations = new CollectionQuery<LSFActionDeclaration>(LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), Collections.singletonList(LSFStubElementTypes.ACTION), getLSFFile(), Condition.TRUE, BaseUtils.immutableCast(getFinalizer()))).findAll();

        return declarations;
    }

    @Override
    public Annotation resolveNotFoundErrorAnnotation(AnnotationHolder holder, Collection<? extends LSFDeclaration> similarDeclarations) {
        String errorText;
        boolean noSuchProperty = similarDeclarations.size() == 0;
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

        Annotation annotation = holder.createErrorAnnotation(getTextRange(), errorText);
        if (!noSuchProperty) {
            annotation.setEnforcedTextAttributes(LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);
        }
        return annotation;
    }
}