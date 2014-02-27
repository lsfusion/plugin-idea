package com.lsfusion.psi;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.psi.stubs.types.indexes.PropIndex;
import org.jetbrains.annotations.NotNull;

public class LSFProjectComponent implements ProjectComponent {
    private final Project project;

    public LSFProjectComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        ((PsiManagerEx) PsiManager.getInstance(project)).registerRunnableToRunOnAnyChange(new Runnable() {
            public void run() {
                LSFResolveCache.getParamClassesInstance().clearCache(true);
                LSFResolveCache.getValueClassInstance().clearCache(true);
            }
        });

        StartupManager.getInstance(project).runWhenProjectIsInitialized(new Runnable() {
            @Override
            public void run() {
                // прогоняем ресолвинг классов параметров свойств на старте, чтобы не терять время на парсинг потом, в частности на Ctrl+Alt+Shift+N
                ProgressManager.getInstance().run(new Task.Backgroundable(project, "Resolving properties' param classes") {
                    @Override
                    public void run(@NotNull final ProgressIndicator indicator) {
                        final PropIndex propIndex = PropIndex.getInstance();
                        for (final String key : propIndex.getAllKeys(project)) {
                            ApplicationManager.getApplication().runReadAction(new Runnable() {
                                @Override
                                public void run() {
                                    for (LSFGlobalPropDeclaration decl : propIndex.get(key, project, GlobalSearchScope.projectScope(project))) {
                                        decl.resolveParamClasses();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "LSFProjectComponent";
    }
}
