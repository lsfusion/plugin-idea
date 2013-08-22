package com.simpleplugin.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.simpleplugin.psi.LSFExtendingFormDeclaration;
import com.simpleplugin.psi.LSFFormDecl;
import com.simpleplugin.psi.LSFFormGroupObjectsList;
import com.simpleplugin.psi.LSFFormTreeGroupObjectList;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.declarations.LSFObjectDeclaration;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.stubs.extend.ExtendFormStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
