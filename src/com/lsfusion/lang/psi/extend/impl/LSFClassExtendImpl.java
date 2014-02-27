package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.references.impl.LSFFullNameReferenceImpl;
import com.lsfusion.lang.psi.stubs.extend.ExtendClassStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class LSFClassExtendImpl extends LSFExtendImpl<LSFClassExtend, ExtendClassStubElement> implements LSFClassExtend {

    public LSFClassExtendImpl(@NotNull ExtendClassStubElement extendClassStubElement, @NotNull IStubElementType nodeType) {
        super(extendClassStubElement, nodeType);
    }

    public LSFClassExtendImpl(@NotNull ASTNode node) {
        super(node);
    }

    public abstract LSFClassDecl getClassDecl();

    @Nullable
    protected abstract LSFClassParentsList getClassParentsList();

    public abstract LSFExtendingClassDeclaration getExtendingClassDeclaration();

    @Nullable
    protected abstract LSFStaticObjectDeclList getStaticObjectDeclList();

    @Override
    public String getGlobalName() {
        ExtendClassStubElement stub = getStub();
        if (stub != null)
            return stub.getGlobalName();

        LSFExtendingClassDeclaration extend = getExtendingClassDeclaration();
        if (extend != null)
            return extend.getCustomClassUsage().getNameRef();

        return getClassDecl().getGlobalName();
    }

    @Override
    public LSFFullNameDeclaration resolveDecl() {
        LSFClassDecl classDecl = getClassDecl();
        if (classDecl != null)
            return classDecl;

        return getExtendingClassDeclaration().getCustomClassUsage().resolveDecl();
    }

    @Nullable
    @Override
    protected LSFFullNameDeclaration getOwnDeclaration() {
        return getClassDecl();
    }

    @Nullable
    @Override
    protected LSFFullNameDeclaration resolveExtendingDeclaration() {
        LSFExtendingClassDeclaration extendingClassDeclaration = getExtendingClassDeclaration();
        if (extendingClassDeclaration != null) {
            return extendingClassDeclaration.getCustomClassUsage().resolveDecl();
        }
        return null;
    }

    @Override
    protected FullNameStubElementType<?, LSFClassDeclaration> getStubType() {
        return LSFStubElementTypes.CLASS;
    }

    @Override
    public List<LSFClassDeclaration> resolveExtends() {
        LSFClassParentsList parents = getClassParentsList();
        if (parents == null)
            return new ArrayList<LSFClassDeclaration>();
        List<LSFClassDeclaration> result = new ArrayList<LSFClassDeclaration>();
        LSFNonEmptyCustomClassUsageList nonEmptyCustomClassUsageList = parents.getNonEmptyCustomClassUsageList();
        if (nonEmptyCustomClassUsageList != null) {
            for (LSFCustomClassUsage usage : nonEmptyCustomClassUsageList.getCustomClassUsageList()) {
                LSFClassDeclaration decl = usage.resolveDecl();
                if (decl != null)
                    result.add(decl);
            }
        }
        return result;
    }

    public List<String> getShortExtends() {
        ExtendClassStubElement stub = getStub();
        if (stub != null)
            return stub.getShortExtends();

        LSFClassParentsList parents = getClassParentsList();
        if (parents == null)
            return new ArrayList<String>();

        List<String> result = new ArrayList<String>();
        LSFNonEmptyCustomClassUsageList nonEmptyCustomClassUsageList = parents.getNonEmptyCustomClassUsageList();
        if (nonEmptyCustomClassUsageList != null) {
            for (LSFCustomClassUsage usage : nonEmptyCustomClassUsageList.getCustomClassUsageList()) {
                result.add(LSFFullNameReferenceImpl.getSimpleName(usage.getCompoundID()).getText());
            }
        }
        return result;
    }

    @Override
    public List<LSFStaticObjectDeclaration> getStaticObjects() {
        LSFStaticObjectDeclList listDecl = getStaticObjectDeclList();
        List<LSFStaticObjectDeclaration> result = new ArrayList<LSFStaticObjectDeclaration>();
        if (listDecl != null && listDecl.getNonEmptyStaticObjectDeclList() != null) {
            for (LSFStaticObjectDecl decl : listDecl.getNonEmptyStaticObjectDeclList().getStaticObjectDeclList()) {
                result.add(decl);
            }
        }
        return result;
    }

    public Set<LSFStaticObjectDeclaration> resolveStaticObjectDuplicates() {
        Set<LSFStaticObjectDeclaration> result = new HashSet<LSFStaticObjectDeclaration>();

        List<LSFStaticObjectDeclaration> localDecls = getStaticObjects();
        for (int i = 0; i < localDecls.size(); i++) {
            LSFStaticObjectDeclaration decl1 = localDecls.get(i);
            for (int j = 0; j < localDecls.size(); j++) {
                if (i != j) {
                    if (decl1.getNameIdentifier().getText().equals(localDecls.get(j).getNameIdentifier().getText())) {
                        result.add(decl1);
                        break;
                    }
                }
            }
        }

        List<LSFStaticObjectDeclaration> parentObjects = new ArrayList<LSFStaticObjectDeclaration>();
        for (LSFClassExtend extend : LSFGlobalResolver.findExtendElements(resolveDecl(), LSFStubElementTypes.EXTENDCLASS, getLSFFile()).findAll()) {
            if (!this.equals(extend)) {
                parentObjects.addAll(extend.getStaticObjects());
            }
        }

        for (LSFStaticObjectDeclaration decl : localDecls) {
            for (LSFStaticObjectDeclaration parentDecl : parentObjects) {
                if (decl.getNameIdentifier().getText().equals(parentDecl.getNameIdentifier().getText())) {
                    result.add(decl);
                    break;
                }
            }
        }
        return result;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.CLASS;
    }
}
