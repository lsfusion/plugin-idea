package com.lsfusion.design;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.lsfusion.design.view.DesignViewFactory;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FormDesignChangeDetector extends PsiTreeChangeAdapter implements ProjectActivity {
    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        PsiManager.getInstance(project).addPsiTreeChangeListener(this, () -> {});
        return Unit.INSTANCE;
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
