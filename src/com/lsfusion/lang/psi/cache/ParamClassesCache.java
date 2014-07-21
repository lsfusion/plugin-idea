package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ParamClassesCache extends PsiDependentCache<LSFPropDeclaration, List<LSFClassSet>> {
    public static final PsiResolver<LSFPropDeclaration, List<LSFClassSet>> RESOLVER = new PsiResolver<LSFPropDeclaration, List<LSFClassSet>>() {
        @Override
        public List<LSFClassSet> resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.resolveParamClassesNoCache();
        }
    };

    public static ParamClassesCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, ParamClassesCache.class);
    }

    public ParamClassesCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }
    
    public List<LSFClassSet> resolveParamClassesWithCaching(LSFPropDeclaration element) {
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}
