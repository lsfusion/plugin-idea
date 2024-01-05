package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ParamClassesCache extends PsiDependentCache<LSFActionOrPropDeclaration, List<LSFExClassSet>> {
    public static final PsiResolver<LSFActionOrPropDeclaration, List<LSFExClassSet>> RESOLVER = new PsiResolver<>() {
        @Override
        public List<LSFExClassSet> resolve(@NotNull LSFActionOrPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.resolveExParamClassesNoCache();
        }

        @Override
        public boolean checkResultClass(Object result) {
            if(!(result instanceof List))
                return false;

            for(Object element : (List)result)
                if(element != null && !(element instanceof LSFExClassSet))
                    return false;
            return true;
        }
    };

    public static ParamClassesCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, ParamClassesCache.class);
    }

    public ParamClassesCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }
    
    public List<LSFExClassSet> resolveParamClassesWithCaching(LSFActionOrPropDeclaration element) {
        return ApplicationManager.getApplication().runReadAction((Computable<List<LSFExClassSet>>) () -> resolveWithCaching(element, RESOLVER, true, false));
    }
}
