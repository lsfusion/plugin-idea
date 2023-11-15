package com.lsfusion.hierarchy.usages;

import com.intellij.ide.hierarchy.CallHierarchyBrowserBase;
import com.intellij.ide.hierarchy.HierarchyBrowser;
import com.intellij.ide.hierarchy.HierarchyProvider;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFUsageHierarchyProvider implements HierarchyProvider {
    @Nullable
    @Override
    public PsiElement getTarget(@NotNull DataContext dataContext) {
        final Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (project == null) return null;

        return CommonDataKeys.PSI_ELEMENT.getData(dataContext);
    }

    @NotNull
    @Override
    public HierarchyBrowser createHierarchyBrowser(PsiElement target) {
        return new LSFUsageHierarchyBrowser(target.getProject(), target);
    }

    @Override
    public void browserActivated(@NotNull HierarchyBrowser hierarchyBrowser) {
        ((LSFUsageHierarchyBrowser) hierarchyBrowser).changeView(CallHierarchyBrowserBase.getCallerType());
    }
}
