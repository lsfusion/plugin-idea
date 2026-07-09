package com.lsfusion.design;

import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.lsfusion.design.view.DesignViewFactory;
import org.jetbrains.annotations.NotNull;

// Registered via the com.intellij.psi.treeChangeListener EP (project-level, auto-disposed) —
// manual PsiManager.addPsiTreeChangeListener would need a parent disposable tied to the project.
public class FormDesignChangeDetector extends PsiTreeChangeAdapter {
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
