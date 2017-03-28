package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ParamClassesCache extends PsiDependentCache<LSFPropDeclaration, List<LSFExClassSet>> {
    public static final PsiResolver<LSFPropDeclaration, List<LSFExClassSet>> RESOLVER = new PsiResolver<LSFPropDeclaration, List<LSFExClassSet>>() {
        @Override
        public List<LSFExClassSet> resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.resolveExParamClassesNoCache();
        }

        @Override
        public boolean checkResultClass(Object result) {
            if(!(result instanceof List))
                return false;

            for(Object element : (List)result)
                if(!(element instanceof LSFExClassSet))
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
    
    public List<LSFExClassSet> resolveParamClassesWithCaching(LSFPropDeclaration element) {
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}
