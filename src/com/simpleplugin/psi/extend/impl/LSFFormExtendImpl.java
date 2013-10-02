package com.simpleplugin.psi.extend.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.simpleplugin.LSFElementGenerator;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.declarations.LSFGroupObjectDeclaration;
import com.simpleplugin.psi.declarations.LSFObjectDeclaration;
import com.simpleplugin.psi.declarations.LSFPropertyDrawDeclaration;
import com.simpleplugin.psi.declarations.impl.LSFPropertyDrawDeclarationImpl;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.stubs.extend.ExtendFormStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFFormExtendImpl extends LSFExtendImpl<LSFFormExtend, ExtendFormStubElement> implements LSFFormExtend {

    public LSFFormExtendImpl(@NotNull ExtendFormStubElement extendFormStubElement, @NotNull IStubElementType nodeType) {
        super(extendFormStubElement, nodeType);
    }

    public LSFFormExtendImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    protected abstract LSFExtendingFormDeclaration getExtendingFormDeclaration();

    @Nullable
    protected abstract LSFFormDecl getFormDecl();

    @NotNull
    protected abstract List<LSFFormGroupObjectsList> getFormGroupObjectsListList();

    @NotNull
    protected abstract List<LSFFormPropertiesList> getFormPropertiesListList();

    @NotNull
    protected abstract List<LSFFormTreeGroupObjectList> getFormTreeGroupObjectListList();

    @Override
    public String getGlobalName() {
        ExtendFormStubElement stub = getStub();
        if(stub!=null)
            return stub.getGlobalName();

        LSFExtendingFormDeclaration extend = getExtendingFormDeclaration();
        if(extend!=null)
            return extend.getFormUsage().getNameRef();
        
        return getFormDecl().getGlobalName(); 
    }

    @Override
    public LSFFullNameDeclaration resolveDecl() {
        LSFFormDecl formDecl = getFormDecl();
        if(formDecl!=null)
            return formDecl;
        
        return getExtendingFormDeclaration().getFormUsage().resolveDecl();
    }

    @Override
    public Collection<LSFObjectDeclaration> getObjectDecls() {
        Collection<LSFObjectDeclaration> result = new ArrayList<LSFObjectDeclaration>();
        for(LSFFormGroupObjectsList formGroupObject : getFormGroupObjectsListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFObjectDeclaration.class));
        for(LSFFormTreeGroupObjectList formGroupObject : getFormTreeGroupObjectListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFObjectDeclaration.class));
        return result;
    }

    @Override
    public Collection<LSFGroupObjectDeclaration> getGroupObjectDecls() {
        Collection<LSFGroupObjectDeclaration> result = new ArrayList<LSFGroupObjectDeclaration>();
        for(LSFFormGroupObjectsList formGroupObject : getFormGroupObjectsListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFGroupObjectDeclaration.class));
        for(LSFFormTreeGroupObjectList formGroupObject : getFormTreeGroupObjectListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formGroupObject, LSFGroupObjectDeclaration.class));
        return result;
    }

    
    @Override
    public Collection<LSFPropertyDrawDeclaration> getPropertyDrawDecls() {
        Collection<LSFPropertyDrawDeclaration> result = new ArrayList<LSFPropertyDrawDeclaration>();
        result.addAll(LSFElementGenerator.getBuiltInFormProps(getProject()));
        for(LSFFormPropertiesList formProperties : getFormPropertiesListList())
            result.addAll(PsiTreeUtil.findChildrenOfType(formProperties, LSFPropertyDrawDeclaration.class));
        return result;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return AllIcons.FileTypes.UiForm;
    }
}
