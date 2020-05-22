package com.lsfusion;

import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public abstract class LSFNameContributor implements ChooseByNameContributorEx {
    protected abstract Collection<StringStubIndexExtension> getIndices();

    // it's hacky here, but all that pattern obtaining is a big hack
    // the problem is that when you press CTRL+SHIFT+N - editor is focused, so we can't get pattern from there, but we'll assume that there will be previous pattern (default idea behaviour)
    private static String prevPattern = null;

    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter) {
        String pattern;
        final Component focusedComponent = WindowManagerEx.getInstanceEx().getFocusedComponent(scope.getProject());
        if (focusedComponent instanceof JTextField) {
            pattern = ((JTextField) focusedComponent).getText();
            prevPattern = pattern;
        } else {
            pattern = prevPattern;
        }
        for (StringStubIndexExtension index : getIndices()) {
            processIndexKey(index, pattern, scope.getProject(), true, processor);
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

    protected void processIndexKey(StringStubIndexExtension index, String pattern, Project project,
                                   boolean includeNonProjectItems, Processor<? super String> processor) {
        Collection<String> allKeys = index.getAllKeys(project);
        for (String allKey : allKeys) {
            processor.process(allKey);
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
