package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.declarations.impl.*;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.DesignStubElementType;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendFormStubElementType;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.function.Function;

public abstract class LSFFormExtendImpl extends LSFExtendImpl<LSFFormExtend, ExtendFormStubElement> implements LSFFormExtend {

    public LSFFormExtendImpl(@NotNull ExtendFormStubElement extendFormStubElement, @NotNull IStubElementType nodeType) {
        super(extendFormStubElement, nodeType);
    }

    public LSFFormExtendImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public abstract LSFExtendingFormDeclaration getExtendingFormDeclaration();

    @NotNull
    protected abstract List<LSFFormGroupObjectsList> getFormGroupObjectsListList();

    @Override
    public String getGlobalName() {
        ExtendFormStubElement stub = getStub();
        if (stub != null)
            return stub.getGlobalName();

        LSFExtendingFormDeclaration extend = getExtendingFormDeclaration();
        if (extend != null)
            return extend.getFormUsageWrapper().getFormUsage().getNameRef();

        LSFFormDecl formDecl = getFormDecl();

        assert formDecl != null;

        return formDecl.getGlobalName();
    }

    @Override
    protected LSFFormDeclaration getOwnDeclaration() {
        return getFormDecl();
    }

    @Nullable
    @Override
    public LSFFullNameReference getExtendingReference() {
        LSFExtendingFormDeclaration extendingFormDeclaration = getExtendingFormDeclaration();
        if (extendingFormDeclaration != null) {
            return extendingFormDeclaration.getFormUsageWrapper().getFormUsage();
        }

        return null;
    }

    @Override
    protected FullNameStubElementType getStubType() {
        return LSFStubElementTypes.FORM;
    }

    @Override
    public Collection<LSFObjectDeclaration> getObjectDecls() {
        Collection<LSFObjectDeclaration> result = new ArrayList<>();
        for (LSFFormGroupObjectsList formGroupObject : getFormGroupObjectsListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFObjectDeclaration.class));
        for (LSFFormTreeGroupObjectList formGroupObject : getFormTreeGroupObjectListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFObjectDeclaration.class));
        return result;
    }

    @Override
    public Collection<LSFGroupObjectDeclaration> getGroupObjectDecls() {
        Collection<LSFGroupObjectDeclaration> result = new ArrayList<>();
        for (LSFFormGroupObjectsList formGroupObject : getFormGroupObjectsListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFGroupObjectDeclaration.class));
        for (LSFFormTreeGroupObjectList formGroupObject : getFormTreeGroupObjectListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFGroupObjectDeclaration.class));
        return result;
    }

    @Override
    public Collection<LSFGroupObjectDeclaration> getGroupObjectNoTreeDecls() {
        Collection<LSFGroupObjectDeclaration> result = new ArrayList<>();
        for (LSFFormGroupObjectsList formGroupObject : getFormGroupObjectsListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFGroupObjectDeclaration.class));
        return result;
    }

    @Override
    public Collection<LSFTreeGroupDeclaration> getTreeGroupDecls() {
        Collection<LSFTreeGroupDeclaration> result = new ArrayList<>();
        for (LSFFormTreeGroupObjectList formGroupObject : getFormTreeGroupObjectListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFTreeGroupDeclaration.class));
        return result;
    }

    @Override
    public Collection<LSFFilterGroupDeclaration> getFilterGroupDecls() {
        Collection<LSFFilterGroupDeclaration> result = new ArrayList<>();
        for (LSFFormFilterGroupDeclaration decl : getFormFilterGroupDeclarationList()) {
            LSFFilterGroupName filterGroup = decl.getFilterGroupName();
            if (filterGroup != null) {
                result.add(filterGroup);
            }
        }
        return result;
    }

    @Override
    public List<LSFFormGroupObjectDeclaration> getFormGroupObjectDeclarations() {
        List<LSFFormGroupObjectDeclaration> result = new ArrayList<>();
        for (LSFFormGroupObjectsList formGroupObject : getFormGroupObjectsListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFFormGroupObjectDeclaration.class));
        return result;
    }

    @Override
    public Collection<LSFPropertyDrawDeclaration> getPropertyDrawDecls() {
        Collection<LSFPropertyDrawDeclaration> result = new ArrayList<>();
        if (getFormDecl() != null) {
            result.addAll(LSFElementGenerator.getBuiltInFormProps(getProject()));
        }
        for (LSFFormPropertiesList formProperties : getFormPropertiesListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formProperties, LSFPropertyDrawDeclaration.class));
        return result;
    }

    private static ExtendFormStubElementType getContextExtendType() {
        return LSFStubElementTypes.EXTENDFORM;
    }

    public static List<PsiElement> processFormImplementationsSearch(LSFFormDeclaration decl) {
        return processImplementationsSearch(decl, getContextExtendType());
    }

    private static Function<PsiElement, FormContext> getContext(boolean objectRef) {
        return current -> current instanceof FormContext && (objectRef || current instanceof LSFFormStatement || current instanceof LSFDesignStatement) ? (FormContext)current : null;
    }

    public static <T extends LSFFormExtendElement> Set<T> processFormContext(PsiElement current, int offset, LSFLocalSearchScope localScope, final Function<LSFFormExtend, Collection<T>> processor) {
        return processContext(current, offset, localScope, processor, getContext(true), FormContext::resolveFormDecl, getContextExtendType());
    }

    public static <T extends LSFFormExtendElement> Set<T> processFormContext(PsiElement current, final Function<LSFFormExtend, Collection<T>> processor, final int offset, LSFLocalSearchScope localScope, boolean objectRef, boolean ignoreUseBeforeDeclarationCheck) {
        return processContext(current, processor, offset, localScope, ignoreUseBeforeDeclarationCheck, getContext(objectRef), FormContext::resolveFormDecl, getContextExtendType());
    }

    @Override
    protected ExtendStubElementType<LSFFormExtend, ExtendFormStubElement> getDuplicateExtendType() {
        return getContextExtendType();
    }

    protected List<Function<LSFFormExtend, Collection<? extends LSFDeclaration>>> getDuplicateProcessors() {
        List<Function<LSFFormExtend, Collection<? extends LSFDeclaration>>> processors = new ArrayList<>();
        processors.add(LSFFormExtend::getGroupObjectDecls);
        processors.add(LSFFormExtend::getPropertyDrawDecls);
        processors.add(LSFFormExtend::getObjectDecls);
        processors.add(LSFFormExtend::getFilterGroupDecls);
        return processors;
    }

    public Set<LSFDeclaration> resolveDuplicates() {
        return resolveDuplicates((Function<LSFFormExtendElement, Condition<LSFFormExtendElement>>) LSFFormExtendElement::getDuplicateCondition,
                decl -> decl instanceof LSFObjectDeclaration && decl.getParent() instanceof LSFFormSingleGroupObjectDeclaration); // we don't want to show duplicate for object in single group object since it will be shown for single group object itself
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.FORM;
    }
}
