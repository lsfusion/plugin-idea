package com.lsfusion;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
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
        for (StringStubIndexExtension index : getIndices()) {
            result.addAll(getIndexKeys(index, project, includeNonProjectItems));
        }
        return result.toArray(new String[result.size()]);
    }

    protected Collection<String> getIndexKeys(StringStubIndexExtension index, Project project, boolean includeNonProjectItems) {
        return index.getAllKeys(project);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        final GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);

        final List<NavigationItem> result = new ArrayList<NavigationItem>();
        for (StringStubIndexExtension index : getIndices()) {
            result.addAll(getItemsFromIndex(index, name, project, scope));
        }
        return result.toArray(new NavigationItem[result.size()]);
    }

    protected Collection<NavigationItem> getItemsFromIndex(StringStubIndexExtension index, String name, Project project, GlobalSearchScope scope) {
        return index.get(name, project, scope);
    }
}
