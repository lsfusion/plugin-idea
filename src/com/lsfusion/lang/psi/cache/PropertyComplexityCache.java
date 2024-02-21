package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PropertyComplexityCache extends PsiDependentCache<LSFPropDeclaration, Integer> {
    public static final PsiResolver<LSFPropDeclaration, Integer> RESOLVER = new PsiResolver<>() {
        @Override
        public Integer resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.getComplexity();
        }

        @Override
        public boolean checkResultClass(Object result) {
            return result instanceof Integer;
        }
    };

    public static PropertyComplexityCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, PropertyComplexityCache.class);
    }
    
    public PropertyComplexityCache(Project project) {
        super(project);
    }

    @Nullable
    public Integer resolveWithCaching(@NotNull LSFPropDeclaration element) {
        return super.resolveWithCaching(element, RESOLVER, false, false);
    }
}
