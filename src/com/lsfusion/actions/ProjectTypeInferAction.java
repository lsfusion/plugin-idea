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
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.indexes.ModuleIndex;
import com.lsfusion.lang.typeinfer.TypeInferer;
import com.lsfusion.refactoring.ElementMigration;
import com.lsfusion.refactoring.MigrationChangePolicy;
import com.lsfusion.refactoring.PropertyMigration;
import com.lsfusion.refactoring.ShortenNamesProcessor;
import com.lsfusion.references.LSFToJavaLanguageInjector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProjectTypeInferAction extends AnAction {

    protected boolean shortenAfter() {
        return false;        
    }
    
    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project myProject = e.getProject();
        final Progressive run = new Progressive() {
            public void run(final @NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        MetaChangeDetector.getInstance(myProject).setMetaEnabled(false, false);

                        MetaTransaction transaction = new MetaTransaction();
                        
                        Collection<String> allKeys = ModuleIndex.getInstance().getAllKeys(myProject);
                        
                        Map<LSFGlobalPropDeclaration, List<LSFClassSet>> propertyDecls = new HashMap<>();
                        
                        int i = 0;
                        System.out.println("Processing param classes...");
                        for (final String module : allKeys) {
                            indicator.setText("Processing param classes : " + module);
                            Collection<LSFModuleDeclaration> moduleDeclarations = ModuleIndex.getInstance().get(module, myProject, GlobalSearchScope.allScope(myProject));
                            for (LSFModuleDeclaration declaration : moduleDeclarations) {
                                LSFFile file = declaration.getLSFFile();
                                indicator.setText2("Statements : " + file.getChildren().length);
                                
                                for(LSFGlobalPropDeclaration decl : PsiTreeUtil.findChildrenOfType(file, LSFGlobalPropDeclaration.class))
                                    propertyDecls.put(decl, decl.resolveParamClasses());
                                
                                indicator.setText2("");
                            }
                            System.out.println(((double) i) / allKeys.size());
                            indicator.setFraction(((double) i++) / allKeys.size());
                        }

                        System.out.println("Processing files...");
                        i = 0;
                        for (final String module : allKeys) {
                            indicator.setText("Processing : " + module);
                            Collection<LSFModuleDeclaration> moduleDeclarations = ModuleIndex.getInstance().get(module, myProject, GlobalSearchScope.allScope(myProject));
                            for (LSFModuleDeclaration declaration : moduleDeclarations) {
                                LSFFile file = declaration.getLSFFile();
                                indicator.setText2("Statements : " + file.getChildren().length);
                                TypeInferer.typeInfer(file, transaction);
                                indicator.setText2("");
                            }
                            System.out.println(((double) i) / allKeys.size());
                            indicator.setFraction(((double) i++) / allKeys.size());
                        }
                        
                        List<ElementMigration> migrations = new ArrayList<>();
                        for(Map.Entry<LSFGlobalPropDeclaration, List<LSFClassSet>> propertyDeclEntry : propertyDecls.entrySet()) {
                            LSFGlobalPropDeclaration decl = propertyDeclEntry.getKey();
                            List<LSFClassSet> oldClasses = propertyDeclEntry.getValue();
                            List<LSFClassSet> newClasses = decl.resolveParamClasses();
                            if(newClasses != null && !newClasses.equals(oldClasses))
                                migrations.add(PropertyMigration.create(decl, oldClasses, newClasses));
                        }
                        ShortenNamesProcessor.modifyMigrationScripts(migrations, MigrationChangePolicy.INCREMENT_VERSION, myProject, ProjectScope.getProjectScope(myProject));

                        transaction.apply();
                    }
                });

                if(shortenAfter()) {
                    MetaChangeDetector.getInstance(myProject).setMetaEnabled(true, true); // переобновим мета коды, потому как могли быть ошибки. нельзя в writeAction'е так как на Thread.sleep'е повиснет
                    MetaChangeDetector.getInstance(myProject).setMetaEnabled(false, false);

                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        public void run() {
                            ShortenNamesProcessor.shortenAllPropNames(myProject);
                        }
                    });
                }
            }
        };
        LSFToJavaLanguageInjector.disableGroupInjections = true;

        final Task task = new Task.Modal(myProject, "Updating metacode", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    public void run() {
                        CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                            public void run() {
                                run.run(indicator);
                                
                                LSFToJavaLanguageInjector.disableGroupInjections = false;
                            }
                        });                                
                    }
                });
            }
        };
        ProgressManager.getInstance().run(task);
    }

}
