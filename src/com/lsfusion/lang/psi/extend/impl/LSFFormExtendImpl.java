package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.declarations.impl.*;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

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
    protected LSFFormDeclaration resolveExtendingDeclaration() {
        LSFExtendingFormDeclaration extendingFormDeclaration = getExtendingFormDeclaration();
        if (extendingFormDeclaration != null) {
            return extendingFormDeclaration.getFormUsageWrapper().getFormUsage().resolveDecl();
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

    public Set<LSFDeclaration> resolveDuplicates() {
        Query<LSFFormExtend> extendForms = LSFGlobalResolver.findExtendElements(resolveDecl(), LSFStubElementTypes.EXTENDFORM, getLSFFile());

        Set<LSFDeclaration> duplicates = new LinkedHashSet<>();
        duplicates.addAll(resolveDuplicates((List<LSFGroupObjectDeclaration>) getGroupObjectDecls(), LSFGroupObjectDeclarationImpl.getProcessor(), extendForms));
        duplicates.addAll(resolveDuplicates((List<LSFPropertyDrawDeclaration>) getPropertyDrawDecls(), LSFPropertyDrawDeclarationImpl.getProcessor(), extendForms));
        duplicates.addAll(resolveDuplicates((List<LSFObjectDeclaration>) getObjectDecls(), LSFObjectDeclarationImpl.getProcessor(), extendForms));
        duplicates.addAll(resolveDuplicates((List<LSFFilterGroupDeclaration>) getFilterGroupDecls(), LSFFilterGroupDeclarationImpl.getProcessor(), extendForms));

        return duplicates;
    }

    private Set<LSFDeclaration> resolveDuplicates(List<? extends LSFDeclaration> localDecls, final LSFFormElementDeclarationImpl.Processor processor, Query<LSFFormExtend> extendForms) {
        Set<LSFDeclaration> duplicates = new LinkedHashSet<>();

        for (int i = 0; i < localDecls.size(); i++) {
            LSFDeclaration decl1 = localDecls.get(i);
            Condition decl1Condition = ((LSFFormExtendElement) decl1).getDuplicateCondition();
            for (int j = i + 1; j < localDecls.size(); j++) {
                LSFDeclaration decl2 = localDecls.get(j);
                if (decl1Condition.value(decl2)) {
                    checkAndAddDuplicate(duplicates, decl1);
                    checkAndAddDuplicate(duplicates, decl2);
                }
            }
        }

        final List<LSFDeclaration> otherDecls = new ArrayList<>();
        if (extendForms != null) {
            extendForms.forEach(new com.intellij.util.Processor<LSFFormExtend>() {
                public boolean process(LSFFormExtend formExtend) {
                    if (!LSFFormExtendImpl.this.equals(formExtend)) {
                        otherDecls.addAll(processor.process(formExtend));
                    }
                    return true;
                }
            });

            for (LSFDeclaration decl1 : localDecls) {
                Condition decl1Condition = ((LSFFormExtendElement) decl1).getDuplicateCondition();
                for (LSFDeclaration decl2 : otherDecls) {
                    if (decl1Condition.value(decl2)) {
                        checkAndAddDuplicate(duplicates, decl1);
                    }
                }
            }
        }

        return duplicates;
    }

    private void checkAndAddDuplicate(Set<LSFDeclaration> duplicates, LSFDeclaration decl) {
        if (decl.getContainingFile().equals(getContainingFile()) &&
                !(decl instanceof LSFObjectDeclaration && decl.getParent() instanceof LSFFormSingleGroupObjectDeclaration)) {// 
            duplicates.add(decl);
        }
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.FORM;
    }
}
