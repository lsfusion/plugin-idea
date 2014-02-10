package com.simpleplugin.psi;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerEx;
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
