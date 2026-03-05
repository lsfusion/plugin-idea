package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.NotNull;

public class ValueClassCache extends PsiDependentCache<LSFActionOrPropDeclaration, LSFExClassSet> {
    public static final PsiResolver<LSFActionOrPropDeclaration, LSFExClassSet> INFER_RESOLVER = new PsiResolver<>() {
        @Override
        public LSFExClassSet resolve(@NotNull LSFActionOrPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.resolveExValueClassNoCache(true);
        }

        @Override
        public boolean checkResultClass(Object result) {
            return result instanceof LSFExClassSet;
        }
    };
    
    public static final PsiResolver<LSFActionOrPropDeclaration, LSFExClassSet> NO_INFER_RESOLVER = new PsiResolver<>() {
        @Override
        public LSFExClassSet resolve(@NotNull LSFActionOrPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.resolveExValueClassNoCache(false);
        }

        @Override
        public boolean checkResultClass(Object result) {
            return result instanceof LSFExClassSet;
        }
    };

    public static ValueClassCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, ValueClassCache.class);
    }

    public ValueClassCache(Project project) {
        super(project);
    }

    public LSFExClassSet resolveValueClassWithCaching(LSFActionOrPropDeclaration element, boolean infer) {
        return resolveWithCaching(element, infer ? INFER_RESOLVER : NO_INFER_RESOLVER, true, false);
    }
}
