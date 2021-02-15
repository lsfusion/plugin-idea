package com.lsfusion.lang.psi;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.LSFLanguage;
import org.jetbrains.annotations.NotNull;

public class LSFFilesSearchScope extends GlobalSearchScope {

    private final PsiManager myPsiManager;

    public LSFFilesSearchScope(@NotNull Project project) {
        super(project);
        myPsiManager = PsiManager.getInstance(project);
    }

    @Override
    public boolean isSearchInModuleContent(@NotNull Module aModule) {
        return true;
    }

    @Override
    public boolean isSearchInLibraries() {
        return true;
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        if (file.isDirectory())
            return false;
        FileViewProvider viewProvider = myPsiManager.findViewProvider(file);
        return viewProvider != null && viewProvider.hasLanguage(LSFLanguage.INSTANCE);
    }
}
