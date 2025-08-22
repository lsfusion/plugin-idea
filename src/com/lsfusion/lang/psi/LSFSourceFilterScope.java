package com.lsfusion.lang.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.DelegatingGlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFSourceFilterScope extends DelegatingGlobalSearchScope {
    private ProjectFileIndex fileIndex;
    private boolean includeLibrarySources;

    public LSFSourceFilterScope(@NotNull GlobalSearchScope baseScope) {
        this(baseScope, false);
    }

    public LSFSourceFilterScope(@NotNull GlobalSearchScope baseScope, boolean includeLibrarySources) {
        super(baseScope);
        Project project = getProject();
        fileIndex = project == null ? null : ProjectFileIndex.getInstance(project);
        this.includeLibrarySources = includeLibrarySources;
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        if (!super.contains(file)) {
            return false;
        }

        if (fileIndex == null) {
            return false;
        }

        if (includeLibrarySources || myBaseScope.isForceSearchingInLibrarySources()) {
            return true;
        }
        return !fileIndex.isInLibrarySource(file);
    }

    @NotNull
    public static LSFSourceFilterScope allScope(@NotNull Project project) {
        return new LSFSourceFilterScope(GlobalSearchScope.allScope(project));
    }

    @Nullable
    public static LSFSourceFilterScope create(GlobalSearchScope baseScope) {
        return baseScope == null ? null : new LSFSourceFilterScope(baseScope);
    }
}
