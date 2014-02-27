package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFElementGenerator;
import com.lsfusion.LSFIcons;
import com.lsfusion.psi.LSFFile;
import com.lsfusion.psi.LSFGlobalResolver;
import com.lsfusion.psi.LSFId;
import com.lsfusion.psi.LSFSimpleNameWithCaption;
import com.lsfusion.psi.declarations.LSFClassDeclaration;
import com.lsfusion.psi.extend.LSFClassExtend;
import com.lsfusion.psi.extend.impl.LSFClassExtendImpl;
import com.lsfusion.psi.references.LSFClassReference;
import com.lsfusion.psi.stubs.ClassStubElement;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFClassDeclarationImpl extends LSFFullNameDeclarationImpl<LSFClassDeclaration, ClassStubElement> implements LSFClassDeclaration {

    public LSFClassDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFClassDeclarationImpl(@NotNull ClassStubElement classStubElement, @NotNull IStubElementType nodeType) {
        super(classStubElement, nodeType);
    }

    protected abstract LSFSimpleNameWithCaption getSimpleNameWithCaption();

    @Override
    public LSFId getNameIdentifier() {
        return getSimpleNameWithCaption().getSimpleName();
    }

    @Override
    public String getQName(PsiElement context) {
        String globalName = getGlobalName();
        LSFClassReference ref = LSFElementGenerator.createClassRefFromText(globalName, null, (LSFFile) context.getContainingFile());
        if (ref.resolveDecl() == this)
            return globalName;
        return getNamespaceName() + "." + globalName;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.CLASS;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.CLASS;
    }

    @Override
    public PsiElement[] processImplementationsSearch() {
        Collection<LSFClassDeclaration> classes = LSFGlobalResolver.findClassExtends(LSFClassDeclarationImpl.this, getProject(), GlobalSearchScope.allScope(getProject()));
        List<PsiElement> names = new ArrayList<PsiElement>();
        for (LSFClassDeclaration classDecl : classes) {
            names.add(classDecl);
        }
        return names.toArray(new PsiElement[names.size()]);
    }

    @Override
    public PsiElement[] processExtensionsSearch() {
        Collection<LSFClassExtend> classes = LSFGlobalResolver.findExtendElements(this, LSFStubElementTypes.EXTENDCLASS, getProject(), GlobalSearchScope.allScope(getProject())).findAll();
        List<PsiElement> names = new ArrayList<PsiElement>();
        for (LSFClassExtend classDecl : classes) {
            if (((LSFClassExtendImpl) classDecl).getClassDecl() == null) {
                names.add(((LSFClassExtendImpl) classDecl).getExtendingClassDeclaration().getCustomClassUsage());
            }
        }
        return names.toArray(new PsiElement[names.size()]);
    }
}
