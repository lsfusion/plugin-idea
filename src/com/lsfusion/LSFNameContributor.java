package com.lsfusion;

import com.intellij.ide.util.gotoByName.ChooseByNameBase;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFNameContributor implements ChooseByNameContributor {
    protected abstract Collection<StringStubIndexExtension> getIndices();
    
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        String pattern = null;
        final Component focusedComponent = WindowManagerEx.getInstanceEx().getFocusedComponent(project);
        if (focusedComponent != null && focusedComponent.getParent() instanceof ChooseByNameBase.JPanelProvider && focusedComponent instanceof JTextField) { // подразумевается ChooseByNameBase.MyTextField
            pattern = ((JTextField) focusedComponent).getText();
        }
        
        List<String> result = new ArrayList<>();
        for (StringStubIndexExtension index : getIndices()) {
            result.addAll(getIndexKeys(index, pattern, project, includeNonProjectItems));
        }
        return result.toArray(new String[result.size()]);
    }

    protected Collection<String> getIndexKeys(StringStubIndexExtension index, String pattern, Project project, boolean includeNonProjectItems) {
        return index.getAllKeys(project);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        final GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);

        final List<NavigationItem> result = new ArrayList<>();
        for (StringStubIndexExtension index : getIndices()) {
            result.addAll(getItemsFromIndex(index, name, project, scope));
        }
        return result.toArray(new NavigationItem[result.size()]);
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
