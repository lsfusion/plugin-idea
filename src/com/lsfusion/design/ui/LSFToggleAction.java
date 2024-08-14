package com.lsfusion.design.ui;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.function.Supplier;

public abstract class LSFToggleAction extends ToggleAction {
    private boolean bgtThread = true;
    
    public LSFToggleAction() {
    }

    public LSFToggleAction(final @Nullable @NlsActions.ActionText String text) {
        super(() -> text);
    }

    public LSFToggleAction(@NotNull Supplier<@NlsActions.ActionText String> text) {
        super(text);
    }

    public LSFToggleAction(final @Nullable @NlsActions.ActionText String text,
                        final @Nullable @NlsActions.ActionDescription String description,
                        final @Nullable Icon icon) {
        super(text, description, icon);
    }

    public LSFToggleAction(@NotNull Supplier<@NlsActions.ActionText String> text,
                        @NotNull Supplier<@NlsActions.ActionDescription String> description,
                        final @Nullable Icon icon) {
        super(text, description, icon);
    }
    
    public LSFToggleAction(@NotNull Supplier<@NlsActions.ActionText String> text, final @Nullable Icon icon) {
        super(text, Presentation.NULL_STRING, icon);
    }
    
    public void setBgtThread(boolean bgtThread) {
        this.bgtThread = bgtThread;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return bgtThread ? ActionUpdateThread.BGT : ActionUpdateThread.EDT;
    }
}
