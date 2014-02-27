package com.lsfusion.actions;

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.navigation.NavigationItem;
import com.intellij.notification.*;
import com.intellij.notification.impl.NotificationsConfigurationImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Progressive;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Segment;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.stubs.types.indexes.ModuleIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class ShowErrorsAction extends AnAction {
    private final String EXCLUDED_MODULES = "EXCLUDED_MODULES_ERRORS_SEARCH";
    
    private Project project;
    final LSFReferenceAnnotator annotator = new LSFReferenceAnnotator();
    
    @Override
    public void actionPerformed(final AnActionEvent e) {
        project = getEventProject(e);
        
        ExcludeModulesDialog dialog = new ExcludeModulesDialog();
        dialog.show();
    }
    
    private void executeSearchTask(final List<Module> excludedModules) {
        final boolean showBalloonsState = NotificationsConfigurationImpl.getNotificationsConfigurationImpl().SHOW_BALLOONS;
        NotificationsConfigurationImpl.getNotificationsConfigurationImpl().SHOW_BALLOONS = false;
        
        final Progressive progress = new Progressive() {
            @Override
            public void run(final @NotNull ProgressIndicator indicator) {

                final Collection<String> allKeys = ApplicationManager.getApplication().runReadAction(new Computable<Collection<String>>() {
                    public Collection<String> compute() {
                        return ModuleIndex.getInstance().getAllKeys(project);
                    }
                });


                Notifications.Bus.notify(new Notification("", "", "Searching for errors started", NotificationType.INFORMATION));

                GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
                for (Module excludedModule : excludedModules) {
                    searchScope = searchScope.intersectWith(GlobalSearchScope.notScope(GlobalSearchScope.moduleScope(excludedModule)));
                }
                final GlobalSearchScope finalSearchScope = searchScope;
                
                int i = 0;
                for (final String module : allKeys) {
                    i++;
                    final int index = i;
                    
                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        @Override
                        public void run() {
                            Collection<LSFModuleDeclaration> moduleDeclarations = ModuleIndex.getInstance().get(module, project, finalSearchScope);
                            for (LSFModuleDeclaration declaration : moduleDeclarations) {

                                LSFFile file = declaration.getLSFFile();

                                indicator.setText("Processing " + index + "/" + allKeys.size() + ": " + file.getName());
                                findErrors(file);
                            }
                        }
                    });
                }
                Notifications.Bus.notify(new Notification("", "", "Searching for errors finished", NotificationType.INFORMATION));
            }
        };

        Task task = new Task.Modal(project, "Searching for errors", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                progress.run(indicator);
            }
        };

        ProgressManager.getInstance().run(task);

        EventLog.getEventLog(project).activate(null);
        
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                NotificationsConfigurationImpl.getNotificationsConfigurationImpl().SHOW_BALLOONS = showBalloonsState;    
            }
        });
    }
    
    private void findErrors(final PsiElement element) {
        annotator.annotate(element, new AnnotationHolderImpl(new AnnotationSession(element.getContainingFile())), true);
        
        if (element instanceof PsiErrorElement) {
            showErrorMessage(element, "Parsing error: " + ((PsiErrorElement) element).getErrorDescription());
        }
        for (PsiElement child : element.getChildren()) {
            findErrors(child);
        }
        
    }
    
    public static void showErrorMessage(final PsiElement element, final String errorMessage) {
        final PsiFile file = element.getContainingFile();
        final Document document = PsiDocumentManager.getInstance(element.getProject()).getDocument(file);
        final SmartPsiElementPointer pointer = SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);
        final Segment range = pointer.getRange();
        int lineNumber = -1;
        int linePosition = -1;
        if (document != null && range != null) {
            lineNumber = document.getLineNumber(range.getStartOffset()) + 1;
            linePosition = range.getStartOffset() - document.getLineStartOffset(lineNumber - 1) + 1;
        }

        final int finalLineNumber = lineNumber;
        final int finalLinePosition = linePosition;
        
        String moduleName = ModuleUtilCore.findModuleForPsiElement(element).getName();
        String linkToTheElement = "<a href=\"\">(" + moduleName + ") " + file.getName() + "(" + finalLineNumber + ":" + finalLinePosition + ")</a>";
        Notifications.Bus.notify(new Notification("LSF errors", linkToTheElement, errorMessage, NotificationType.ERROR, new NotificationListener.Adapter() {
            @Override
            protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent e) {
                ((NavigationItem) element).navigate(true);
            }
        }));
    }
    
    private class ExcludeModulesDialog extends DialogWrapper {
        private JTextField modulesToExclude;

        protected ExcludeModulesDialog() {
            super(project);
            init();
            setTitle("Excluding modules");
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return modulesToExclude;
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            JLabel label = new JLabel("Modules to be excluded from scanning:");

            modulesToExclude = new JTextField(PropertiesComponent.getInstance(project).getValue(EXCLUDED_MODULES));
            modulesToExclude.setColumns(30);
            panel.add(label);
            panel.add(modulesToExclude);
            return panel;
        }

        @Override
        protected void doOKAction() {
            PropertiesComponent.getInstance(project).setValue(EXCLUDED_MODULES, modulesToExclude.getText());
            StringTokenizer tokenizer = new StringTokenizer(modulesToExclude.getText(), ";");
            final List<Module> modules = new ArrayList<Module>();
            ModulesConfigurator modulesConfigurator = new ModulesConfigurator(project);
            while(tokenizer.hasMoreElements()) {
                String moduleName = tokenizer.nextToken();
                Module module = modulesConfigurator.getModule(moduleName);
                if (module != null) {
                    modules.add(module);
                }
            }
            super.doOKAction();
            executeSearchTask(modules);
        }
    }
}
