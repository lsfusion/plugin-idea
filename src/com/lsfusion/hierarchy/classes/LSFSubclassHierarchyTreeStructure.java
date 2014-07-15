package com.lsfusion.hierarchy.classes;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ArrayListSet;
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.stubs.extend.types.indexes.ClassExtendsClassIndex;
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
        Set<LSFClassHierarchyNodeDescriptor> result = new ArrayListSet<LSFClassHierarchyNodeDescriptor>();

        LSFClassDeclaration classDecl = ((LSFClassHierarchyNodeDescriptor) descriptor).getClassDecl();
        ClassExtendsClassIndex index = ClassExtendsClassIndex.getInstance();
        GlobalSearchScope useScope = (GlobalSearchScope) classDecl.getUseScope();
        Collection<LSFClassExtend> classExtends = index.get(classDecl.getName(), myProject, useScope);

        for (LSFClassExtend ext : classExtends) {
            if (ext.resolveExtends().contains(classDecl)) {
                LSFClassDeclaration decl = ((LSFClassStatement) ext).getClassDecl();
                if (decl == null) {
                    decl = ((LSFClassStatement) ext).getExtendingClassDeclaration().getCustomClassUsageWrapper().getCustomClassUsage().resolveDecl();
                }
                if (decl != null && !decl.equals(classDecl)) {
                    result.add(new LSFClassHierarchyNodeDescriptor(myProject, descriptor, decl, false));
                }
            }
        }

        return result.toArray();
    }
}
