package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;

public class ColumnNameCache extends PsiDependentCache<LSFGlobalPropDeclaration, String> {
    public static final PsiResolver<LSFGlobalPropDeclaration, String> RESOLVER = new PsiResolver<>() {
        @Override
        public String resolve(@NotNull LSFGlobalPropDeclaration globalProp, boolean incompleteCode) {
            return globalProp.getColumnNameNoCache();
        }

        @Override
        public boolean checkResultClass(Object result) {
            return result instanceof String;
        }
    };
    
    public static ColumnNameCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, ColumnNameCache.class);
    }

    public ColumnNameCache(Project project) {
        super(project);
    }

    public String getColumnNameWithCaching(LSFGlobalPropDeclaration element) {
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}
