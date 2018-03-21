package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ParamClassesCache extends PsiDependentCache<LSFActionOrPropDeclaration, List<LSFExClassSet>> {
    public static final PsiResolver<LSFActionOrPropDeclaration, List<LSFExClassSet>> RESOLVER = new PsiResolver<LSFActionOrPropDeclaration, List<LSFExClassSet>>() {
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
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}
