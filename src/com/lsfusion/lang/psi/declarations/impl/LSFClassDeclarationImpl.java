package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.containers.ArrayListSet;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.impl.LSFClassExtendImpl;
import com.lsfusion.lang.psi.references.LSFClassReference;
import com.lsfusion.lang.psi.stubs.ClassStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.refactoring.ClassMigration;
import com.lsfusion.refactoring.ElementMigration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

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

    @Override
    public List<String> getSNames() {
        return Collections.singletonList(getDeclName());
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
        List<PsiElement> names = new ArrayList<>();

        Collection<LSFClassExtend> extendClasses = LSFGlobalResolver.findExtendElements(this, LSFStubElementTypes.EXTENDCLASS, getProject(), GlobalSearchScope.allScope(getProject())).findAll();
        for (LSFClassExtend classExtend : extendClasses) {
            if (((LSFClassExtendImpl) classExtend).getClassDecl() == null) {
                names.add(((LSFClassExtendImpl) classExtend).getExtendingClassDeclaration().getCustomClassUsageWrapper());
            }
        }

        names.addAll(processChildrenSearch(this, getProject()));

        return names.toArray(new PsiElement[names.size()]);
    }

    public static Set<LSFClassDeclaration> processChildrenSearch(LSFClassDeclaration classDecl, Project project) {
        Set<LSFClassDeclaration> result = new ArrayListSet<>();
        Collection<LSFClassDeclaration> classExtends = LSFGlobalResolver.findClassExtends(classDecl, project, GlobalSearchScope.allScope(project));
        for (LSFClassDeclaration lsfClassDeclaration : classExtends) {
            result.add(lsfClassDeclaration);
            result.addAll(processChildrenSearch(lsfClassDeclaration, project));
        }
        return result;
    }

    @Override
    public String getCaption() {
        LSFLocalizedStringLiteral stringLiteral = getSimpleNameWithCaption().getLocalizedStringLiteral();
        if (stringLiteral != null) {
            return stringLiteral.getValue();
        }
        return null;
    }

    @Override
    public LSFClassSet getUpSet() {
        return new CustomClassSet(this);
    }

    @Override
    public ElementMigration getMigration(String newName) {
        return new ClassMigration(this, getName(), newName);
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }
}
