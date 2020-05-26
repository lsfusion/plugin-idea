package com.lsfusion;

import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class LSFNameContributor implements ChooseByNameContributorEx {


    protected abstract Collection<StringStubIndexExtension> getIndices();


    protected Processor<? super String> getProcessor(StringStubIndexExtension index,  Processor<? super String> processor,
                                                     GlobalSearchScope scope) {
        return processor;
    }

    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter) {

        for (StringStubIndexExtension index : getIndices()) {
            index.processAllKeys(scope.getProject(), getProcessor(index, processor, scope));
        }
    }

    @Override
    public void processElementsWithName(@NotNull String name, @NotNull Processor<? super NavigationItem> processor, @NotNull FindSymbolParameters parameters) {

        for (StringStubIndexExtension index : getIndices()) {
            Collection<NavigationItem> navigationItems = getItemsFromIndex(index, name, parameters.getProject(), parameters.getSearchScope());
            for (NavigationItem itemsFromIndex : navigationItems) {
                processor.process(itemsFromIndex);
            }
        }
    }

    protected Collection<NavigationItem> getItemsFromIndex(StringStubIndexExtension index, String name, Project project, GlobalSearchScope scope) {
        return index.get(name, project, scope);
    }

    protected boolean matches(String name, String pattern) {
        MinusculeMatcher totalMatcher = NameUtil.buildMatcher(pattern, NameUtil.MatchingCaseSensitivity.NONE);
        if (totalMatcher.matches(name)) {
            return true;
        }

        String[] words = pattern.split("(?=\\p{Upper})");

        String cutPattern = pattern;
        for (int i = words.length - 1; i > 0; i--) {
            String word = words[i];
            cutPattern = cutPattern.substring(0, cutPattern.lastIndexOf(word));
            MinusculeMatcher matcher = NameUtil.buildMatcher(cutPattern, NameUtil.MatchingCaseSensitivity.NONE);
            if (matcher.matches(name)) {
                return true;
            }
        }
        return false;
    }
}
