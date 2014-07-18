package com.lsfusion;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.indexes.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LSFSymbolContributor extends LSFNameContributor {
    // полагаем, что состояние не меняется между вызовами getNames() и getItemsByName()
    // оба вызова осуществляются в рамках одного метода. (см. DefaultChooseByNameItemProvider.filterElements(), SearchEverywhereAction.buildSymbols())
    private Map<String, List<NavigationItem>> propertyDeclarationsMap = new HashMap<String, List<NavigationItem>>();

    @Override
    protected Collection<StringStubIndexExtension> getIndices() {
        List<StringStubIndexExtension> indices = new ArrayList<StringStubIndexExtension>();
        indices.add(ClassIndex.getInstance());
        indices.add(ModuleIndex.getInstance());
        indices.add(ExplicitNamespaceIndex.getInstance());
        indices.add(MetaIndex.getInstance());
        indices.add(PropIndex.getInstance());
        indices.add(FormIndex.getInstance());
        indices.add(GroupIndex.getInstance());
        indices.add(TableIndex.getInstance());
        indices.add(WindowIndex.getInstance());
        indices.add(NavigatorElementIndex.getInstance());
        indices.add(ComponentIndex.getInstance());
        return indices;
    }

    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        propertyDeclarationsMap = new HashMap<String, List<NavigationItem>>();
        return super.getNames(project, includeNonProjectItems);
    }

    @Override
    protected Collection<String> getIndexKeys(StringStubIndexExtension index, Project project, boolean includeNonProjectItems) {
        if (index instanceof PropIndex) {
            List<String> result = new ArrayList<String>();
            Collection<String> allKeys = index.getAllKeys(project);
            final GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);
            for (String key : allKeys) {
                for (LSFGlobalPropDeclaration decl : ((PropIndex) index).get(key, project, scope)) {
                    List<LSFClassSet> paramClasses = decl.resolveParamClasses();
                    String paramsString = "";
                    if (paramClasses != null) {
                        for (LSFClassSet classSet : paramClasses) {
                            if (classSet != null) {
                                paramsString += classSet;
                            }
                        }
                    }

                    String withParams = key + paramsString;
                    List<NavigationItem> decls = propertyDeclarationsMap.get(withParams);
                    if (decls == null) {
                        decls = new ArrayList<NavigationItem>();
                        propertyDeclarationsMap.put(withParams, decls);
                    }
                    decls.add((NavigationItem) decl);
                    result.add(withParams);
                }
            }
            return result;
        } else {
            return super.getIndexKeys(index, project, includeNonProjectItems);
        }
    }

    @Override
    protected Collection<NavigationItem> getItemsFromIndex(StringStubIndexExtension index, String name, Project project, GlobalSearchScope scope) {
        if (index instanceof PropIndex) {
            List<NavigationItem> decls = propertyDeclarationsMap.get(name);
            return decls != null
                   ? decls
                   : super.getItemsFromIndex(index, name, project, scope);
        } else {
            return super.getItemsFromIndex(index, name, project, scope);
        }
    }
}
