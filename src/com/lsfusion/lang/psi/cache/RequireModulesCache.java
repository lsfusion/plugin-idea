package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RequireModulesCache extends PsiDependentCache<LSFModuleDeclaration, Set<VirtualFile>> {
    public static final PsiResolver<LSFModuleDeclaration, Set<VirtualFile>> RESOLVER = new PsiResolver<LSFModuleDeclaration, Set<VirtualFile>>() {
        @Override
        public Set<VirtualFile> resolve(@NotNull LSFModuleDeclaration lsfModuleDeclaration, boolean incompleteCode) {
            return LSFGlobalResolver.getRequireModulesNoCache(lsfModuleDeclaration);
        }

        @Override
        public boolean checkResultClass(Object result) {
            if(!(result instanceof Set))
                return false;

            for(Object element : (Set)result)
                if(!(element instanceof VirtualFile))
                    return false;
            return true;

        }
    };

    public static RequireModulesCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, RequireModulesCache.class);
    }

    public RequireModulesCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    public Set<VirtualFile> getRequireModulesWithCaching(LSFModuleDeclaration element) {
        Set<VirtualFile> lsfFiles = resolveWithCaching(element, RESOLVER, true, false);
        if (lsfFiles == null) // System
            return new HashSet<>();
        return lsfFiles;
    }
}
