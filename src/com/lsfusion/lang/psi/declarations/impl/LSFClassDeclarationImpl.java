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
        return getSimpleNameWithCaption() != null ? getSimpleNameWithCaption().getSimpleName() : null;
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

        names.addAll(LSFClassExtendImpl.processClassImplementationsSearch(this));
        names.addAll(processChildrenSearch(this, getProject()));

        return names.toArray(new PsiElement[0]);
    }

    public static Set<LSFClassExtend> processChildrenSearch(LSFClassDeclaration classDecl, Project project) {
        Set<LSFClassExtend> result = new ArrayListSet<>();
        Map<LSFClassExtend, LSFClassDeclaration> classExtends = LSFGlobalResolver.findChildrenExtendsMap(classDecl, project, GlobalSearchScope.allScope(project));
        for (Map.Entry<LSFClassExtend, LSFClassDeclaration> entry : classExtends.entrySet()) {
            result.add(entry.getKey());
            result.addAll(processChildrenSearch(entry.getValue(), project));
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
