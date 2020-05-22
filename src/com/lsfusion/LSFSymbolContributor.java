package com.lsfusion;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.Processor;
import com.intellij.util.indexing.IdFilter;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.indexes.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LSFSymbolContributor extends LSFNameContributor {
    // полагаем, что состояние не меняется между вызовами getNames() и getItemsByName()
    // оба вызова осуществляются в рамках одного метода. (см. DefaultChooseByNameItemProvider.filterElements(), SearchEverywhereAction.buildSymbols())
    private final Map<String, List<NavigationItem>> propertyDeclarationsMap = new HashMap<>();

    private void clearPropertyDeclarationsMap() {
        synchronized (propertyDeclarationsMap) {
            propertyDeclarationsMap.clear();
        }

    }

    private List<NavigationItem> getPropertyDeclarationsMap(String withParams, boolean putIfIsEmpty) {
        synchronized (propertyDeclarationsMap) {
            List<NavigationItem> decls = propertyDeclarationsMap.get(withParams);
            if (putIfIsEmpty && decls == null) {
                decls = Collections.synchronizedList(new ArrayList<NavigationItem>());
                propertyDeclarationsMap.put(withParams, decls);
            }
            return decls;
        }
    }

    @Override
    protected Collection<StringStubIndexExtension> getIndices() {
        List<StringStubIndexExtension> indices = new ArrayList<>();
        indices.add(ClassIndex.getInstance());
        indices.add(ModuleIndex.getInstance());
        indices.add(ExplicitNamespaceIndex.getInstance());
        indices.add(MetaIndex.getInstance());
        indices.add(PropIndex.getInstance());
        indices.add(ActionIndex.getInstance());
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
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter) {
        clearPropertyDeclarationsMap();
        super.processNames(processor, scope, filter);
    }

    private static class ActionOrPropFullNavigationItem implements NavigationItem {
        private final String fullName;
        private final NavigationItem decl;

        public ActionOrPropFullNavigationItem(String fullName, LSFActionOrGlobalPropDeclaration decl) {
            this.fullName = fullName;
            this.decl = (NavigationItem) decl;
        }

        @Nullable
        @Override
        public String getName() {
            return fullName;
        }

        @Nullable
        @Override
        public ItemPresentation getPresentation() {
            return decl.getPresentation();
        }

        @Override
        public void navigate(boolean requestFocus) {
            decl.navigate(requestFocus);
        }

        @Override
        public boolean canNavigate() {
            return decl.canNavigate();
        }

        @Override
        public boolean canNavigateToSource() {
            return decl.canNavigateToSource();
        }
    }

    @Override
    protected void processIndexKey(StringStubIndexExtension index, String pattern, Project project,
                                   boolean includeNonProjectItems, Processor<? super String> processor) {

        final GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);
        if (index instanceof ActionOrPropIndex) {
            Collection<String> allKeys = index.getAllKeys(project);
            for (String key : allKeys) {
                if (pattern != null && !matches(key, pattern)) {
                    continue;
                }
                for (LSFActionOrGlobalPropDeclaration decl : ((ActionOrPropIndex<?>) index).get(key, project, scope)) {
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
                    List<NavigationItem> decls = getPropertyDeclarationsMap(withParams, true);
                    decls.add(new ActionOrPropFullNavigationItem(withParams, decl));
                    processor.process(withParams);
                }
            }
        } else {
            super.processIndexKey(index, pattern, project, includeNonProjectItems, processor);
        }
    }

    @Override
    protected Collection<NavigationItem> getItemsFromIndex(StringStubIndexExtension index, String name, Project project, GlobalSearchScope scope) {
        if (index instanceof ActionOrPropIndex) {
            List<NavigationItem> decls = getPropertyDeclarationsMap(name, false);
            return decls != null
                   ? decls
                   : super.getItemsFromIndex(index, name, project, scope);
        } else {
            return super.getItemsFromIndex(index, name, project, scope);
        }
    }
}
