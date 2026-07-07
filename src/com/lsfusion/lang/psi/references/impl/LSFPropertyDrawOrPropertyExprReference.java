package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFResolvingError;
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
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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

        LSFResolveResult propertyResult = resolvePropertyResult();
        if (propertyResult != null && propertyResult.declarations.size() > 1) {
            return new LSFResolveResult(propertyResult.declarations, new LSFResolveResult.AmbigiousErrorAnnotator(this, propertyResult.declarations));
        }

        LSFResolveResult result = super.resolveNoCache();
        if (!result.declarations.isEmpty() || getAliasUsage() != null) {
            return result;
        }

        // no form property draw -> use the (single) global property, if any
        if (propertyResult != null && !propertyResult.declarations.isEmpty()) {
            return new LSFResolveResult(propertyResult.declarations);
        }
        return result;
    }

    @Override
    public LSFResolvingError resolveAmbiguousErrorAnnotation(Collection<? extends LSFDeclaration> declarations) {
        return new LSFResolvingError(this, getAmbiguousReferenceText(declarations), true);
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

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        // The reference can resolve to either a propertyDraw declaration (when the
        // property is exposed via PROPERTIES on the form and matched by name+objects)
        // or to the underlying property declaration (when only FILTERS/ORDERS refer
        // to it via the propertyExpr-shorthand fallback in resolvePropertyDeclaration).
        // Without LSFPropDeclaration here, isReferenceTo skips the reference when
        // the renamed element is the property itself, leaving FILTERS-only usages
        // out of the rename refactoring.
        return element instanceof LSFPropertyDrawDeclaration || element instanceof LSFPropDeclaration;
    }

    @Nullable
    private LSFDeclaration resolveTargetDeclaration() {
        LSFResolveResult result = multiResolveDecl(true);
        return result == null ? null : LSFResolveUtil.singleResolve(result.declarations);
    }

    @Nullable
    private LSFResolveResult resolvePropertyResult() {
        LSFFormPropertyDrawPropertyUsage propertyUsage = getFormPropertyDrawPropertyUsage();
        if (propertyUsage == null) {
            return null;
        }

        LSFId nameId = propertyUsage.getSimpleName();
        if (nameId == null) { // a predefined operator usage (NEW[Class], EDIT, ...) is a form draw, not a property expression
            return null;
        }

        List<LSFClassSet> usageClasses = LSFPsiImplUtil.resolveParamClasses(getObjectUsageList());
        return LSFElementGenerator
                .createPropRefFromText(nameId.getText(), null, getLSFFile(), null, usageClasses, false, false)
                .multiResolveDecl(true);
    }
}
