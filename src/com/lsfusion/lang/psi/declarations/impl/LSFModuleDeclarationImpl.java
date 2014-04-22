package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import com.lsfusion.lang.psi.stubs.ModuleStubElement;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// extends NamespaceDeclaration, при assertion'е что getNamespaceName==null
public abstract class LSFModuleDeclarationImpl extends LSFNamespaceDeclarationImpl<LSFModuleDeclaration, ModuleStubElement> implements LSFModuleDeclaration {

    protected LSFModuleDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFModuleDeclarationImpl(@NotNull ModuleStubElement moduleStubElement, @NotNull IStubElementType nodeType) {
        super(moduleStubElement, nodeType);
    }

    @NotNull
    protected abstract LSFModuleName getModuleName();

    @Nullable
    protected abstract LSFRequireList getRequireList();

    @Nullable
    protected abstract LSFPriorityList getPriorityList();

    @Nullable
    protected abstract LSFNamespaceName getNamespaceName();

    public LSFNamespaceReference getExplicitNamespaceRef() {
        ModuleStubElement stub = getStub();
        if(stub != null)
            return stub.getExplicitNamespaceRef();
                
        LSFNamespaceName namespace = getNamespaceName();
        if(namespace==null)
            return null;
        return namespace.getNamespaceUsage();
    }
    
    @Override
    public String getNamespace() {
        LSFNamespaceReference explicitNamespace = getExplicitNamespaceRef();
        if(explicitNamespace==null)
            return getDeclName();
        return explicitNamespace.getNameRef();
    }

    @Override
    public List<LSFNamespaceReference> getPriorityRefs() {
        ModuleStubElement stub = getStub();
        if(stub != null)
            return stub.getPriorityRefs();
        
        LSFPriorityList priorityList = getPriorityList();
        if(priorityList==null)
            return new ArrayList<LSFNamespaceReference>();
        LSFNonEmptyNamespaceUsageList nonEmptyNamespaceUsageList = priorityList.getNonEmptyNamespaceUsageList();
        if (nonEmptyNamespaceUsageList == null) {
            return Collections.emptyList();
        }
        return BaseUtils.<LSFNamespaceReference, LSFNamespaceUsage>immutableCast(nonEmptyNamespaceUsageList.getNamespaceUsageList());
    }

    @Override
    public boolean requires(LSFModuleDeclaration module) {
        return LSFGlobalResolver.getRequireModules(this).contains(module.getLSFFile());
//        for (LSFModuleReference ref : getRequireRefs()) {
//            LSFModuleDeclaration decl = ref.resolveDecl();
//            if (decl != null && (module.equals(decl) || decl.requires(module))) {
//                return true;
//            }
//        }
//        return false;
    }

    @Override
    public List<LSFModuleReference> getRequireRefs() {
        ModuleStubElement stub = getStub();
        if(stub != null)
            return stub.getRequireRefs();
        
        LSFRequireList requireList = getRequireList();
        if(requireList==null)
            return new ArrayList<LSFModuleReference>();
        LSFNonEmptyModuleUsageList nonEmptyModuleUsageList = requireList.getNonEmptyModuleUsageList();
        if (nonEmptyModuleUsageList == null) {
            return Collections.emptyList();
        }
        return BaseUtils.<LSFModuleReference, LSFModuleUsage>immutableCast(nonEmptyModuleUsageList.getModuleUsageList());
    }

    @Override
    public LSFSimpleName getNameIdentifier() {
        return getModuleName().getSimpleName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.MODULE;
    }
}
