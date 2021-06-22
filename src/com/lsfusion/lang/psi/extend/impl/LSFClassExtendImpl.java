package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.references.LSFClassReference;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.references.LSFReference;
import com.lsfusion.lang.psi.stubs.extend.ExtendClassStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendClassStubElementType;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendFormStubElementType;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.function.Function;

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
        if (extend != null && extend.getCustomClassUsageWrapper() != null)
            return extend.getCustomClassUsageWrapper().getCustomClassUsage().getNameRef();

        return getClassDecl().getGlobalName();
    }

    @Override
    public LSFFullNameDeclaration resolveDecl() {
        ExtendClassStubElement stub = getStub();
        if(stub != null) {
            return stub.getThis().resolveDecl(getLSFFile());
        }

        LSFClassDecl classDecl = getClassDecl();
        if (classDecl != null)
            return classDecl;

        return getExtendingClassDeclaration().getCustomClassUsageWrapper() != null ?
                getExtendingClassDeclaration().getCustomClassUsageWrapper().getCustomClassUsage().resolveDecl() :
                null;
    }

    @Nullable
    @Override
    protected LSFFullNameDeclaration getOwnDeclaration() {
        return getClassDecl();
    }

    @Nullable
    @Override
    public LSFFullNameReference getExtendingReference() {
        LSFExtendingClassDeclaration extendingClassDeclaration = getExtendingClassDeclaration();
        if (extendingClassDeclaration != null && extendingClassDeclaration.getCustomClassUsageWrapper() != null) {
            return extendingClassDeclaration.getCustomClassUsageWrapper().getCustomClassUsage();
        }
        return null;
    }

    @Override
    protected FullNameStubElementType<?, LSFClassDeclaration> getStubType() {
        return LSFStubElementTypes.CLASS;
    }

    @Override
    public List<LSFClassDeclaration> resolveExtends() {
        ExtendClassStubElement stub = getStub();
        if(stub != null) {
            return LSFStringClassRef.resolveDecls(stub.getExtends(), getLSFFile());
        }

        List<LSFClassDeclaration> result = new ArrayList<>();
        
        LSFClassParentsList parents = getClassParentsList();
        if (parents != null) {
            LSFNonEmptyCustomClassUsageList nonEmptyCustomClassUsageList = parents.getNonEmptyCustomClassUsageList();
            if (nonEmptyCustomClassUsageList != null) {
                for (LSFCustomClassUsage usage : nonEmptyCustomClassUsageList.getCustomClassUsageList()) {
                    LSFClassDeclaration decl = usage.resolveDecl();
                    if (decl != null) 
                        result.add(decl);
                }
            }
        }
        LSFStaticObjectDeclList staticObjectDeclList = getStaticObjectDeclList();
        if(staticObjectDeclList != null) {
            LSFNonEmptyStaticObjectDeclList nonEmptyStaticObjectDeclList = staticObjectDeclList.getNonEmptyStaticObjectDeclList();
            if(nonEmptyStaticObjectDeclList != null) {
                LSFClassDeclaration decl = LSFElementGenerator.getStaticObjectClassRef(getProject()).resolveDecl();
                if(decl != null)
                    result.add(decl);
            }
        }

        return result;
    }

    @Override
    public LSFStringClassRef getThis() {
        ExtendClassStubElement stub = getStub();
        if (stub != null)
            return stub.getThis();

        LSFClassDecl classDecl = getClassDecl();
        if (classDecl != null && classDecl.getSimpleNameWithCaption() != null)
            return new LSFStringClassRef(null, false, classDecl.getSimpleNameWithCaption().getSimpleName().getText());

        return getExtendingClassDeclaration() != null && getExtendingClassDeclaration().getCustomClassUsageWrapper() != null ?
                LSFPsiImplUtil.getClassNameRef(getExtendingClassDeclaration().getCustomClassUsageWrapper().getCustomClassUsage()) :
                null;
    }

    public List<LSFStringClassRef> getExtends() {
        ExtendClassStubElement stub = getStub();
        if (stub != null) 
            return stub.getExtends();

        List<LSFStringClassRef> result = new ArrayList<>();

        LSFClassParentsList parents = getClassParentsList();
        if (parents != null) {
            LSFNonEmptyCustomClassUsageList nonEmptyCustomClassUsageList = parents.getNonEmptyCustomClassUsageList();
            if (nonEmptyCustomClassUsageList != null) {
                for (LSFCustomClassUsage usage : nonEmptyCustomClassUsageList.getCustomClassUsageList()) {
                    result.add(LSFPsiImplUtil.getClassNameRef(usage));
                }
            }
        }

        LSFStaticObjectDeclList staticObjectDeclList = getStaticObjectDeclList();
        if(staticObjectDeclList != null) {
            LSFNonEmptyStaticObjectDeclList nonEmptyStaticObjectDeclList = staticObjectDeclList.getNonEmptyStaticObjectDeclList();
            if(nonEmptyStaticObjectDeclList != null)
                result.add(new LSFStringClassRef("System", "StaticObject"));
        }

        return result;
    }

    @Override
    public List<LSFStaticObjectDeclaration> getStaticObjects() {
        LSFStaticObjectDeclList listDecl = getStaticObjectDeclList();
        List<LSFStaticObjectDeclaration> result = new ArrayList<>();
        if (listDecl != null && listDecl.getNonEmptyStaticObjectDeclList() != null) {
            for (LSFStaticObjectDecl decl : listDecl.getNonEmptyStaticObjectDeclList().getStaticObjectDeclList()) {
                result.add(decl);
            }
        }
        return result;
    }

    private static ExtendClassStubElementType getContextExtendType() {
        return LSFStubElementTypes.EXTENDCLASS;
    }

    public static List<PsiElement> processClassImplementationsSearch(LSFClassDeclaration decl) {
        return processImplementationsSearch(decl, getContextExtendType());
    }

    public static <T extends LSFDeclaration> Set<T> processClassContext(LSFClassDeclaration decl, LSFFile file, int offset, LSFLocalSearchScope localScope, final Function<LSFClassExtend, Collection<T>> processor) {
        // we don't want to respect offset, since static objects are parsed in separate step
        return processContext(decl, file, getContextExtendType(), processor, null, localScope, false);
    }

    protected List<Function<LSFClassExtend, Collection<? extends LSFDeclaration>>> getDuplicateProcessors() {
        List<Function<LSFClassExtend, Collection<? extends LSFDeclaration>>> processors = new ArrayList<>();
        processors.add(LSFClassExtend::getStaticObjects);
        return processors;
    }

    protected ExtendStubElementType<LSFClassExtend, ExtendClassStubElement> getDuplicateExtendType() {
        return getContextExtendType();
    }

    public Set<LSFDeclaration> resolveDuplicates() {
        return resolveDuplicates(LSFStaticObjectDeclaration::getDuplicateCondition, decl -> false);
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.CLASS;
    }
    
    public String getClassName() {
        LSFClassDecl decl = getClassDeclaration();
        return decl == null ? null : decl.getName();
    }
    
    public String getClassNamespace() {
        LSFClassDecl decl = getClassDeclaration();
        return decl == null ? null : decl.getNamespaceName();        
    }
    
    private LSFClassDecl getClassDeclaration() {
        LSFClassDecl decl = getClassDecl();
        if (decl == null) {
            LSFExtendingClassDeclaration extend = getExtendingClassDeclaration();
            if (extend != null && extend.getCustomClassUsageWrapper() != null) {
                LSFCustomClassUsage classUsage = extend.getCustomClassUsageWrapper().getCustomClassUsage();
                return (LSFClassDecl) classUsage.resolveDecl();
            }
        }
        return decl;
    }
}
