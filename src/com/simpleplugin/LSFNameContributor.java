package com.simpleplugin;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.util.ArrayUtil;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.stubs.types.indexes.ClassIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFNameContributor implements ChooseByNameContributor {

    protected abstract Collection<StringStubIndexExtension> getIndices();
    
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        List<String> result = new ArrayList<String>();
        for(StringStubIndexExtension index : getIndices())
            result.addAll(index.getAllKeys(project));
        return result.toArray(new String[result.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        final GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);

        final List<LSFDeclaration> result = new ArrayList<LSFDeclaration>();
        for(StringStubIndexExtension index : getIndices())
            result.addAll(index.get(name, project, scope));
        return result.toArray(new NavigationItem[result.size()]);
    }
}
