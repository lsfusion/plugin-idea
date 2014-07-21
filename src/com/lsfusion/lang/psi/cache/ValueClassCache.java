package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import org.jetbrains.annotations.NotNull;

public class ValueClassCache extends PsiDependentCache<LSFPropDeclaration, LSFClassSet> {
    public static final PsiResolver<LSFPropDeclaration, LSFClassSet> INFER_RESOLVER = new PsiResolver<LSFPropDeclaration, LSFClassSet>() {
        @Override
        public LSFClassSet resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.resolveValueClassNoCache(true);
        }
    };
    
    public static final PsiResolver<LSFPropDeclaration, LSFClassSet> NO_INFER_RESOLVER = new PsiResolver<LSFPropDeclaration, LSFClassSet>() {
        @Override
        public LSFClassSet resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.resolveValueClassNoCache(false);
        }
    };

    public static ValueClassCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, ValueClassCache.class);
    }

    public ValueClassCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    public LSFClassSet resolveValueClassWithCaching(LSFPropDeclaration element, boolean infer) {
        return resolveWithCaching(element, infer ? INFER_RESOLVER : NO_INFER_RESOLVER, true, false);
    }
}
