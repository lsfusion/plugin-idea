package com.simpleplugin.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.extend.LSFClassExtend;
import com.simpleplugin.psi.stubs.extend.ExtendClassStubElement;
import com.simpleplugin.psi.stubs.extend.ExtendFormStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class LSFClassExtendImpl extends LSFExtendImpl<LSFClassExtend, ExtendClassStubElement> implements LSFClassExtend {

    public LSFClassExtendImpl(@NotNull ExtendClassStubElement extendClassStubElement, @NotNull IStubElementType nodeType) {
        super(extendClassStubElement, nodeType);
    }

    public LSFClassExtendImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFClassDecl getClassDecl();

    @Nullable
    protected abstract LSFClassParentsList getClassParentsList();

    protected abstract LSFExtendingClassDeclaration getExtendingClassDeclaration();

    @Override
    public String getGlobalName() {
        ExtendClassStubElement stub = getStub();
        if(stub!=null)
            return stub.getGlobalName();

        LSFExtendingClassDeclaration extend = getExtendingClassDeclaration();
        if(extend!=null)
            return extend.getCustomClassUsage().getNameRef();

        return getClassDecl().getGlobalName();
    }

    @Override
    public LSFFullNameDeclaration resolveDecl() {
        LSFClassDecl classDecl = getClassDecl();
        if(classDecl!=null)
            return classDecl;

        return getExtendingClassDeclaration().getCustomClassUsage().resolveDecl();
    }

    @Override
    public List<LSFClassDeclaration> resolveExtends() {
        LSFClassParentsList parents = getClassParentsList();
        if(parents==null)
            return new ArrayList<LSFClassDeclaration>();
        List<LSFClassDeclaration> result = new ArrayList<LSFClassDeclaration>();
        for(LSFCustomClassUsage usage : parents.getNonEmptyCustomClassUsageList().getCustomClassUsageList()) {
            LSFClassDeclaration decl = usage.resolveDecl();
            if(decl!=null)
                result.add(decl);
        }
        return result;
    }
}
