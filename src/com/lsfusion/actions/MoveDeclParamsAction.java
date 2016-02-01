package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Progressive;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.lsfusion.lang.typeinfer.TypeInferer;
import com.lsfusion.refactoring.ShortenNamesProcessor;
import com.lsfusion.references.LSFToJavaLanguageInjector;
import org.jetbrains.annotations.NotNull;

public class MoveDeclParamsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project myProject = e.getProject();
        final Progressive run = new Progressive() {
            public void run(final @NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        TypeInferer.remove(myProject);
                    }
                });
            }
        };

        Task task = new Task.Modal(myProject, "Shortening names", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    public void run() {
                        CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                            public void run() {
                                run.run(indicator);
                            }
                        });}
                });
            }
        };
        ProgressManager.getInstance().run(task);
    }
}