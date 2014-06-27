package com.lsfusion.hierarchy.classes;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ArrayListSet;
import com.lsfusion.lang.psi.LSFClassParentsList;
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.LSFCustomClassUsage;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class LSFSuperclassHierarchyTreeStructure extends HierarchyTreeStructure {
    public LSFSuperclassHierarchyTreeStructure(Project project, LSFClassDeclaration classDecl) {
        super(project, new LSFClassHierarchyNodeDescriptor(project, null, classDecl, true));
    }

    @NotNull
    @Override
    protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
        LSFClassHierarchyNodeDescriptor nodeDescriptor = (LSFClassHierarchyNodeDescriptor) descriptor;
        Set<LSFClassHierarchyNodeDescriptor> result = new ArrayListSet<LSFClassHierarchyNodeDescriptor>();

        Collection<LSFClassExtend> classExtends = LSFGlobalResolver.findExtendElements(nodeDescriptor.getClassDecl(), LSFStubElementTypes.EXTENDCLASS, myProject, GlobalSearchScope.allScope(myProject)).findAll();
        for (LSFClassExtend classExtend : classExtends) {
            result.addAll(createNode(classExtend, descriptor));
        }

        return result.toArray();
    }

    private List<LSFClassHierarchyNodeDescriptor> createNode(LSFClassExtend classExtend, HierarchyNodeDescriptor parent) {
        List<LSFClassHierarchyNodeDescriptor> result = new ArrayList<LSFClassHierarchyNodeDescriptor>();

        LSFClassParentsList classParentsList = ((LSFClassStatement) classExtend).getClassParentsList();
        if (classParentsList != null) {
            for (LSFCustomClassUsage lsfCustomClassUsage : classParentsList.getNonEmptyCustomClassUsageList().getCustomClassUsageList()) {
                result.add(new LSFClassHierarchyNodeDescriptor(myProject, parent, lsfCustomClassUsage.resolveDecl(), false));
            }
        }

        return result;
    }
}
