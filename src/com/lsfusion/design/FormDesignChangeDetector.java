package com.lsfusion.design;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.lsfusion.design.view.DesignViewFactory;
import org.jetbrains.annotations.NotNull;

public class FormDesignChangeDetector extends PsiTreeChangeAdapter implements ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        PsiManager.getInstance(project).addPsiTreeChangeListener(this, () -> {});
    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    private void fireChildChanged(PsiTreeChangeEvent event) {
        DesignViewFactory.getInstance().designCodeChanged(event);
    }
}
