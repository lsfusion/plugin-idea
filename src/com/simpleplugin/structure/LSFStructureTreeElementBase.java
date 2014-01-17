package com.simpleplugin.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.simpleplugin.classes.CustomClassSet;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.psi.LSFClassDecl;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.declarations.LSFExplicitInterfacePropStatement;
import com.simpleplugin.psi.stubs.types.indexes.ExplicitInterfaceIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LSFStructureTreeElementBase extends PsiTreeElementBase<PsiElement> {
    private PsiElement baseElement;
    private List<StructureViewTreeElement> children;

    protected LSFStructureTreeElementBase(PsiElement psiElement) {
        super(psiElement.getContainingFile());
        baseElement = psiElement;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        if (children == null) {
            children = new ArrayList<StructureViewTreeElement>();

            if (baseElement instanceof LSFClassDecl) {
                children.addAll(getPropertyTreeElements((LSFClassDecl) baseElement));

                for (LSFClassDeclaration decl : CustomClassSet.getParentsRecursively((LSFClassDeclaration) baseElement)) {
                    children.addAll(getPropertyTreeElements((LSFClassDecl) decl));
                }
            }
        }

        return children;
    }

    private Collection<LSFPropertyStatementTreeElement> getPropertyTreeElements(LSFClassDecl classDecl) {
        String name = classDecl.getName();
        Project project = classDecl.getProject();
        Collection<LSFExplicitInterfacePropStatement> statements = ExplicitInterfaceIndex.getInstance().get(name, project, GlobalSearchScope.allScope(project));

        List<LSFPropertyStatementTreeElement> result = new ArrayList<LSFPropertyStatementTreeElement>();

        for (LSFExplicitInterfacePropStatement statement : statements) {
            for (LSFClassSet classSet : statement.getPropertyStatement().resolveParamClasses()) {
                if (classSet != null) {
                    LSFValueClass paramClass = classSet.getCommonClass();
                    if (paramClass instanceof LSFClassDecl && paramClass == classDecl) {
                        result.add(new LSFPropertyStatementTreeElement(classDecl, statement));
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return null;
    }
}
