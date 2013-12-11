package com.simpleplugin.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.simpleplugin.LSFElementGenerator;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.*;
import com.simpleplugin.psi.declarations.impl.*;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.stubs.extend.ExtendFormStubElement;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
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

    @Nullable
    public abstract LSFFormDecl getFormDecl();

    @NotNull
    protected abstract List<LSFFormGroupObjectsList> getFormGroupObjectsListList();

    @NotNull
    protected abstract List<LSFFormPropertiesList> getFormPropertiesListList();

    @NotNull
    protected abstract List<LSFFormTreeGroupObjectList> getFormTreeGroupObjectListList();

    @Override
    public String getGlobalName() {
        ExtendFormStubElement stub = getStub();
        if (stub != null)
            return stub.getGlobalName();

        LSFExtendingFormDeclaration extend = getExtendingFormDeclaration();
        if (extend != null)
            return extend.getFormUsage().getNameRef();

        return getFormDecl().getGlobalName();
    }

    @Override
    public LSFFullNameDeclaration resolveDecl() {
        LSFFormDecl formDecl = getFormDecl();
        if (formDecl != null)
            return formDecl;

        return getExtendingFormDeclaration().getFormUsage().resolveDecl();
    }

    @Override
    public Collection<LSFObjectDeclaration> getObjectDecls() {
        Collection<LSFObjectDeclaration> result = new ArrayList<LSFObjectDeclaration>();
        for (LSFFormGroupObjectsList formGroupObject : getFormGroupObjectsListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFObjectDeclaration.class));
        for (LSFFormTreeGroupObjectList formGroupObject : getFormTreeGroupObjectListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFObjectDeclaration.class));
        return result;
    }

    @Override
    public Collection<LSFGroupObjectDeclaration> getGroupObjectDecls() {
        Collection<LSFGroupObjectDeclaration> result = new ArrayList<LSFGroupObjectDeclaration>();
        for (LSFFormGroupObjectsList formGroupObject : getFormGroupObjectsListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFGroupObjectDeclaration.class));
        for (LSFFormTreeGroupObjectList formGroupObject : getFormTreeGroupObjectListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFGroupObjectDeclaration.class));
        return result;
    }


    @Override
    public Collection<LSFPropertyDrawDeclaration> getPropertyDrawDecls() {
        Collection<LSFPropertyDrawDeclaration> result = new ArrayList<LSFPropertyDrawDeclaration>();
        result.addAll(LSFElementGenerator.getBuiltInFormProps(getProject()));
        for (LSFFormPropertiesList formProperties : getFormPropertiesListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formProperties, LSFPropertyDrawDeclaration.class));
        return result;
    }

    public Set<LSFDeclaration> resolveDuplicates() {
        Query<LSFFormExtend> extendForms = LSFGlobalResolver.findExtendElements(resolveDecl(), LSFStubElementTypes.EXTENDFORM, getLSFFile());

        Set<LSFDeclaration> duplicates = new LinkedHashSet<LSFDeclaration>();
        duplicates.addAll(resolveDuplicates((List<LSFGroupObjectDeclaration>) getGroupObjectDecls(), LSFGroupObjectDeclarationImpl.getProcessor(), extendForms));
        duplicates.addAll(resolveDuplicates((List<LSFPropertyDrawDeclaration>) getPropertyDrawDecls(), LSFPropertyDrawDeclarationImpl.getProcessor(), extendForms));
        duplicates.addAll(resolveDuplicates((List<LSFObjectDeclaration>) getObjectDecls(), LSFObjectDeclarationImpl.getProcessor(), extendForms));

        return duplicates;
    }

    private Set<LSFDeclaration> resolveDuplicates(List<? extends LSFDeclaration> localDecls, final LSFFormElementDeclarationImpl.Processor processor, Query<LSFFormExtend> extendForms) {
        Set<LSFDeclaration> duplicates = new LinkedHashSet<LSFDeclaration>();

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

        final List<LSFDeclaration> otherDecls = new ArrayList<LSFDeclaration>();
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
