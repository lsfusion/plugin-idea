package com.lsfusion.typeinfer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Progressive;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.meta.MetaTransaction;
import com.lsfusion.psi.LSFFile;
import com.lsfusion.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.psi.stubs.types.indexes.ModuleIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class FindNotInferredAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project myProject = e.getProject();
        final Progressive run = new Progressive() {
            public void run(final @NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        MetaTransaction transaction = new MetaTransaction();

                        Collection<String> allKeys = ModuleIndex.getInstance().getAllKeys(myProject);

                        int i = 0;
                        for (final String module : allKeys) {
                            indicator.setText("Processing : " + module);
                            Collection<LSFModuleDeclaration> moduleDeclarations = ModuleIndex.getInstance().get(module, myProject, GlobalSearchScope.allScope(myProject));
                            for (LSFModuleDeclaration declaration : moduleDeclarations) {
                                LSFFile file = declaration.getLSFFile();
                                indicator.setText2("Statements : " + file.getChildren().length);
                                TypeInferer.findNotInferred(file);
                                indicator.setText2("");
                            }
                            System.out.println(((double) i) / allKeys.size());
                            indicator.setFraction(((double) i++) / allKeys.size());
                        }

                        transaction.apply();
                    }
                });
            }
        };
        Task task = new Task.Modal(myProject, "Finding not inferred", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    public void run() {
                        run.run(indicator);
                    }
                });
            }
        };
        ProgressManager.getInstance().run(task);
    }
}
