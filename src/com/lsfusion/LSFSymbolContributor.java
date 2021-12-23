package com.lsfusion;

import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
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
                decls = Collections.synchronizedList(new ArrayList<>());
                propertyDeclarationsMap.put(withParams, decls);
            }
            return decls;
        }
    }

    @Override
    protected Collection<LSFStringStubIndex> getIndices() {
        List<LSFStringStubIndex> indices = new ArrayList<>();
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
//        indices.add(ComponentIndex.getInstance());
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
    protected Processor<? super String> getProcessor(LSFStringStubIndex index,
                                                     Processor<? super String> processor,
                                                     GlobalSearchScope scope) {

        String pattern = scope.getProject().getUserData(ChooseByNamePopup.CURRENT_SEARCH_PATTERN);

        if (index instanceof ActionOrPropIndex) {
            return (Processor<String>) s -> {
                if (pattern != null && matches(s, pattern)) {
                    for (LSFActionOrGlobalPropDeclaration decl : getItemsFromIndex((ActionOrPropIndex<?>) index, s, scope.getProject(), GlobalSearchScope.allScope(scope.getProject()))) {
                        List<LSFClassSet> paramClasses = decl.resolveParamClasses();
                        String paramsString = "";
                        if (paramClasses != null) {
                            for (LSFClassSet classSet : paramClasses) {
                                if (classSet != null) {
                                    paramsString += classSet;
                                }
                            }
                        }
                        String withParams = s + paramsString;
                        List<NavigationItem> decls = getPropertyDeclarationsMap(withParams, true);
                        decls.add(new ActionOrPropFullNavigationItem(withParams, decl));
                        processor.process(withParams);
                    }
                    return true;
                }
                return true;
            };
        }
        return super.getProcessor(index, processor, scope);
    }

    @Override
    protected Collection<NavigationItem> getItemsWithParamsFromIndex(LSFStringStubIndex index, String name, Project project, GlobalSearchScope scope) {
        if (index instanceof ActionOrPropIndex) {
            List<NavigationItem> decls = getPropertyDeclarationsMap(name, false);
            return decls != null
                    ? decls
                    : super.getItemsWithParamsFromIndex(index, name, project, scope);
        } else {
            return super.getItemsWithParamsFromIndex(index, name, project, scope);
        }
    }
}
