package com.lsfusion.hierarchy.classes;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ArrayListSet;
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.indexes.ClassExtendsClassIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class LSFSubclassHierarchyTreeStructure extends HierarchyTreeStructure {
    public LSFSubclassHierarchyTreeStructure(Project project, LSFClassDeclaration classDecl) {
        super(project, new LSFClassHierarchyNodeDescriptor(project, null, classDecl, true));
    }

    @NotNull
    @Override
    protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
        LSFClassDeclaration classDecl = ((LSFClassHierarchyNodeDescriptor) descriptor).getClassDecl();
        GlobalSearchScope useScope = (GlobalSearchScope) classDecl.getUseScope();

        Set<LSFClassHierarchyNodeDescriptor> result = new ArrayListSet<>();
        for(LSFClassDeclaration decl : LSFGlobalResolver.findClassExtends(classDecl, myProject, useScope))
            if (!decl.equals(classDecl))
                result.add(new LSFClassHierarchyNodeDescriptor(myProject, descriptor, decl, false));
        return result.toArray();
    }
}
