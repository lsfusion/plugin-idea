package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.declarations.impl.LSFFormExtendElement;
import com.lsfusion.lang.psi.extend.LSFFormContextExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.lsfusion.lang.psi.stubs.extend.FormContextExtendStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.FormContextExtendStubElementType;
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

    @NotNull
    protected abstract List<LSFFormFormsList> getFormFormsListList();

    @Override
    public String getGlobalName() {
        ExtendFormStubElement stub = getStub();
        if (stub != null)
            return stub.getGlobalName();

        LSFExtendingFormDeclaration extend = getExtendingFormDeclaration();
        if (extend != null) {
            LSFFormUsageWrapper wrapper = extend.getFormUsageWrapper();
            if(wrapper != null) {
                return wrapper.getFormUsage().getNameRef();
            }
        }

        LSFFormDecl formDecl = getFormDecl();
        return formDecl != null ? formDecl.getGlobalName() : null;
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
            LSFFormUsageWrapper formUsageWrapper = extendingFormDeclaration.getFormUsageWrapper();
            if(formUsageWrapper != null) {
                return formUsageWrapper.getFormUsage();
            }
        }

        return null;
    }

    @Override
    protected FullNameStubElementType getStubType() {
        return LSFStubElementTypes.FORM;
    }

    @Override
    public Collection<LSFFormFormsDeclaration> getFormDecls() {
        Collection<LSFFormFormsDeclaration> result = new ArrayList<>();
        for (LSFFormFormsList formGroupObject : getFormFormsListList())
            result.addAll(formGroupObject.getFormFormsListItemList());
        return result;
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

    public static List<LSFFullNameReference> processFormImplementationsSearch(LSFFormDeclaration decl) {
        return processImplementationsSearch(decl);
    }

    public static <T extends LSFDeclaration, Extend extends LSFFormContextExtend<Extend, Stub>, Stub extends FormContextExtendStubElement<Extend, Stub>>
                    Set<T> processFormContext(PsiElement current, final int offset, LSFLocalSearchScope localScope, final Function<Extend, Collection<T>> processor, boolean ignoreUseBeforeDeclarationCheck,
                                              FormContextExtendStubElementType<Extend, Stub> type, boolean objectRef) {
        FormContext context = current instanceof FormContext && (objectRef || current instanceof LSFFormStatement || current instanceof LSFDesignStatement) ? (FormContext) current : null;
        if (context != null) {
            LSFFile file = (LSFFile) current.getContainingFile();
            Set<T> finalResult = new HashSet<>();
            for(LSFFormDeclaration formDecl : findFormElements(context, file, offset, localScope, ignoreUseBeforeDeclarationCheck))
                finalResult.addAll(processContext(formDecl, file, type, processor, offset, localScope, ignoreUseBeforeDeclarationCheck));
            return finalResult;
        }
        return null;
    }

    public static <T extends LSFDeclaration, Extend extends LSFFormContextExtend<Extend, Stub>, Stub extends FormContextExtendStubElement<Extend, Stub>>
                Set<T> processFormContext(FormContext formContext, final int offset, LSFLocalSearchScope localScope, final Function<Extend, Collection<T>> processor, boolean ignoreUseBeforeDeclarationCheck,
                              FormContextExtendStubElementType<Extend, Stub> type) {
        LSFFile file = (LSFFile) formContext.getContainingFile();
        Set<T> finalResult = new HashSet<>();
        for(LSFFormDeclaration formDecl : findFormElements(formContext, file, offset, localScope, ignoreUseBeforeDeclarationCheck))
            finalResult.addAll(processContext(formDecl, file, type, processor, offset, localScope, ignoreUseBeforeDeclarationCheck));
        return finalResult;
    }

    private static Set<LSFFormDeclaration> findFormElements(FormContext formContext, LSFFile file, Integer offset, LSFLocalSearchScope localScope, boolean ignoreUseBeforeDeclarationCheck) {
        Set<LSFFormDeclaration> formDecls = new HashSet<>();
        LSFFormDeclaration formDecl = formContext.resolveFormDecl();
        if(formDecl != null)
            formDecls.add(formDecl);

        for(LSFFormFormsDeclaration aggrForm : processContext(formDecl, file, null, LSFFormExtend::getFormDecls, offset, localScope, ignoreUseBeforeDeclarationCheck))
            formDecls.addAll(findFormElements(aggrForm, file, offset, localScope, ignoreUseBeforeDeclarationCheck));

        return formDecls;
    }

    public static <T extends LSFDeclaration, This extends LSFFormContextExtend<This, Stub>, Stub extends FormContextExtendStubElement<This, Stub>>
                    Set<T> processFormContext(PsiElement current, int offset, LSFLocalSearchScope localScope, final Function<This, Collection<T>> processor,
                                              FormContextExtendStubElementType<This, Stub> type) {
        FormContext formContext = PsiTreeUtil.getParentOfType(current, FormContext.class, false);
        if(formContext != null)
            return processFormContext(formContext, offset, localScope, processor, false, type);
        return new HashSet<>();
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
