package com.lsfusion.hierarchy.usages;

import com.intellij.find.findUsages.DefaultFindUsagesHandlerFactory;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ArrayListSet;
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFormStatement;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.usage.LSFUsageFilteringRuleProvider;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class LSFUsageHierarchyTreeStructure extends HierarchyTreeStructure {
    public LSFUsageHierarchyTreeStructure(@NotNull Project project, @NotNull PsiElement target) {
        super(project, new LSFUsageHierarchyNodeDescriptor(project, null, target, true));
    }

    @NotNull
    @Override
    protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
        Set<LSFUsageHierarchyNodeDescriptor> result = new ArrayListSet<LSFUsageHierarchyNodeDescriptor>();
        final PsiElement element = ((LSFUsageHierarchyNodeDescriptor) descriptor).getElementId();

        if (element != null && element instanceof LSFId) {
            DefaultFindUsagesHandlerFactory fact = new DefaultFindUsagesHandlerFactory();
            FindUsagesHandler findUsagesHandler = fact.createFindUsagesHandler(element, false);

            if (findUsagesHandler != null) {
                Collection<PsiReference> res = findUsagesHandler.findReferencesToHighlight(element, GlobalSearchScope.allScope(myProject));

                for (PsiReference ref : res) {
                    PsiElement el = LSFPsiUtils.getStatementParent(ref.getElement());
                    if (el != null) {
                        PsiElement nodeElement = ((LSFUsageHierarchyNodeDescriptor) descriptor).getNodeElement();
                        if (!ignore(nodeElement, el)) {
                            result.add(new LSFUsageHierarchyNodeDescriptor(myProject, descriptor, el, false));
                        }
                    }
                }
            }
        }

        return result.toArray();
    }

    private boolean ignore(PsiElement parent, PsiElement child) {
        return (LSFUsageFilteringRuleProvider.filterOut(parent, child) ||
                (parent instanceof LSFFormStatement || parent instanceof LSFDesignStatement) && child instanceof LSFDesignStatement) ||
                (parent instanceof LSFDesignStatement && child instanceof LSFFormStatement) ||
                (parent instanceof LSFClassStatement && (child instanceof LSFClassStatement && (((LSFClassStatement) child).getClassParentsList() != null)));
    }
}
