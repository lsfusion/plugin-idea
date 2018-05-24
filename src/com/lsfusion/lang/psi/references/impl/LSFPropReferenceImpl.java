package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CollectionQuery;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFLocalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.psi.references.LSFPropReference;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class LSFPropReferenceImpl extends LSFActionOrPropReferenceImpl<LSFPropDeclaration, LSFGlobalPropDeclaration> implements LSFPropReference {

    public LSFPropReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Collection<FullNameStubElementType> getStubElementTypes() {
        return Arrays.asList(LSFStubElementTypes.STATEMENTPROP, LSFStubElementTypes.AGGRPARAMPROP);
    }

    protected FullNameStubElementType getStubElementType() {
        throw new UnsupportedOperationException();
    }

    public boolean isImplement() {
        PropertyUsageContext usageContext = getPropertyUsageContext();
        if(usageContext instanceof LSFMappedPropertyClassParamDeclare)
            return usageContext.getParent() instanceof LSFOverridePropertyStatement;
        return false;
    }

    @Override
    public PsiElement getWrapper() {
        return PsiTreeUtil.getParentOfType(this, LSFPropertyUsageWrapper.class);
    }

    private static class LocalResolveProcessor implements PsiScopeProcessor {

        private final String name;
        private Collection<LSFLocalPropDeclaration> found = new ArrayList<>();
        private final Condition<LSFPropDeclaration> condition;

        private LocalResolveProcessor(String name, Condition<LSFPropDeclaration> condition) {
            this.name = name;
            this.condition = condition;
        }

        @Override
        public boolean execute(@NotNull PsiElement element, ResolveState state) {
            if (element instanceof LSFLocalDataPropertyDefinition) {
                LSFNonEmptyLocalPropertyDeclarationNameList declList = ((LSFLocalDataPropertyDefinition) element).getNonEmptyLocalPropertyDeclarationNameList();
                if (declList != null) {
                    List<LSFLocalPropertyDeclarationName> nameList = declList.getLocalPropertyDeclarationNameList();
                    for (LSFLocalPropertyDeclarationName name : nameList) {
                        String nameStr = name.getName();
                        if (nameStr != null && nameStr.equals(this.name) && condition.value(name)) {
                            found.add(name);
                        }
                    }
                }
            }
            return true;
        }

        @Nullable
        @Override
        public <T> T getHint(@NotNull Key<T> hintKey) {
            return null;
        }

        @Override
        public void handleEvent(Event event, @Nullable Object associated) {
        }
    }

    @Override
    protected Collection<? extends LSFPropDeclaration> resolveDeclarations() {
        Collection<? extends LSFPropDeclaration> declarations = BaseUtils.emptyList();

        if (getFullNameRef() == null) {
            declarations = resolveLocals(BaseUtils.immutableCast(getCondition()), getFinalizer());
            if (declarations.isEmpty() && canBeUsedInDirect()) {
                declarations = resolveLocals(getInDirectCondition(), Finalizer.EMPTY);
            }
        }
        
        if (declarations.isEmpty()) {
            declarations = super.resolveDeclarations();
            if (declarations.isEmpty() && canBeUsedInDirect()) {
                declarations = LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getStubElementTypes(), getLSFFile(), BaseUtils.<Condition<LSFGlobalPropDeclaration>>immutableCast(getInDirectCondition()), Finalizer.EMPTY);
            }
        }        
        return declarations;
    }

    @Override
    protected Collection<? extends LSFPropDeclaration> resolveNoConditionDeclarations() {
        Collection<? extends LSFPropDeclaration> declarations = BaseUtils.emptyList();

        final List<LSFClassSet> usageClasses = getUsageContext();
        if (usageClasses != null) {
            Finalizer<LSFPropDeclaration> noConditionFinalizer = getNoConditionFinalizer(usageClasses);

            if (getFullNameRef() == null)
                declarations = resolveLocals(Condition.TRUE, BaseUtils.immutableCast(noConditionFinalizer));
            
            if(declarations.isEmpty())
                declarations = new CollectionQuery<LSFPropDeclaration>(LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getStubElementTypes(), getLSFFile(), Condition.TRUE, BaseUtils.<Finalizer>immutableCast(noConditionFinalizer))).findAll();
        }
        return declarations;
    }

    private Collection<? extends LSFPropDeclaration> resolveLocals(Condition<LSFPropDeclaration> condition, Finalizer<LSFGlobalPropDeclaration> finalizer) {
        LocalResolveProcessor processor = new LocalResolveProcessor(getNameRef(), BaseUtils.<Condition<LSFPropDeclaration>>immutableCast(condition));
        PsiTreeUtil.treeWalkUp(processor, this, null, new ResolveState());
        Finalizer<LSFLocalPropDeclaration> castFinalizer = BaseUtils.immutableCast(finalizer);
        return castFinalizer.finalize(processor.found);
    }

    public boolean isNoContext(PropertyUsageContext usageContext) {
        return usageContext instanceof LSFNoContextPropertyUsage || usageContext instanceof LSFNoContextActionOrPropertyUsage;
    }

    @Override
    protected String getErrorName() {
        return "property";
    }
}
