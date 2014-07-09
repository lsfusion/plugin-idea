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
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.stubs.types.indexes.ModuleIndex;
import com.lsfusion.refactoring.LSFRenameFullNameProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ProjectShortenNamesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project myProject = e.getProject();
        final Progressive run = new Progressive() {
            public void run(final @NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        MetaTransaction transaction = new MetaTransaction();

                        Collection<String> allKeys = ModuleIndex.getInstance().getAllKeys(myProject);
                        Collection<LSFFile> files = new ArrayList<LSFFile>();

                        for (final String module : allKeys) {
                            Collection<LSFModuleDeclaration> moduleDeclarations = ModuleIndex.getInstance().get(module, myProject, GlobalSearchScope.allScope(myProject));
                            for (LSFModuleDeclaration declaration : moduleDeclarations)
                                files.add(declaration.getLSFFile());
                        }
                        
                        LSFRenameFullNameProcessor.shortenAllPropNames(files, transaction);

                        transaction.apply();
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