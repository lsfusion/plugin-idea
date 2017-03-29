package com.lsfusion.actions;

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
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
import com.intellij.openapi.util.Segment;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.text.StringTokenizer;
import com.lsfusion.lang.LSFErrorLevel;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.references.LSFHighlightVisitorImpl;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ShowErrorsAction extends AnAction {
    private final String EXCLUDED_MODULES = "EXCLUDED_MODULES_ERRORS_SEARCH";
    private final String INCLUDE_LSF_FILES = "INCLUDE_LSF_FILES_ERRORS_SEARCH";
    private final String INCLUDE_JAVA_FILES = "INCLUDE_JAVA_FILES_ERRORS_SEARCH";
    private final String INCLUDE_JRXML_FILES = "INCLUDE_JRXML_FILES_ERRORS_SEARCH";
    private final String WARNINGS_SEARCH_MODE = "WARNINGS_SEARCH_MODE";
    private final LSFReferenceAnnotator ANNOTATOR = new LSFReferenceAnnotator();

    private Project project;
    private boolean includeLSFFiles = true;
    private boolean includeJavaFiles = false;
    private boolean includeJrxmlFiles = false;
    private boolean warningsSearchMode = false;

    @Override
    public void actionPerformed(final AnActionEvent e) {
        project = getEventProject(e);

        if (!MetaChangeDetector.getInstance(project).getMetaEnabled()) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Meta code is disabled. The action won't be performed", "Errors search", JOptionPane.INFORMATION_MESSAGE);
        } else {
            ExcludeModulesDialog dialog = new ExcludeModulesDialog();
            dialog.show();
        }
    }

    private void executeSearchTask(final List<String> excludedModules) {
        final boolean showBalloonsState = NotificationsConfigurationImpl.getInstanceImpl().SHOW_BALLOONS;
        NotificationsConfigurationImpl.getInstanceImpl().SHOW_BALLOONS = false;

        final Progressive progress = new Progressive() {
            @Override
            public void run(final @NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        Notifications.Bus.notify(new Notification("", "", "Searching for errors started", NotificationType.INFORMATION));

                        // Почему-то, если использовать просто GlobalSearchScope.projectScope(project), при исключении модулей подключает ещё и все библиотеки
                        GlobalSearchScope searchScope = GlobalSearchScope.notScope(GlobalSearchScope.notScope(GlobalSearchScope.projectScope(project)));

                        ModulesConfigurator modulesConfigurator = new ModulesConfigurator(project);
                        for (String moduleName : excludedModules) {
                            Module module = modulesConfigurator.getModule(moduleName);
                            if (module != null) {
                                searchScope = searchScope.intersectWith(GlobalSearchScope.notScope(module.getModuleScope()));
                            }
                        }

                        final List<VirtualFile> files = new ArrayList<>();

                        if (includeLSFFiles) {
                            files.addAll(FileTypeIndex.getFiles(LSFFileType.INSTANCE, searchScope));
                        }
                        if (includeJavaFiles) {
                            files.addAll(FileTypeIndex.getFiles(JavaFileType.INSTANCE, searchScope));
                        }
                        if (includeJrxmlFiles) {
                            Collection<VirtualFile> xmlFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, searchScope);
                            for (VirtualFile xmlFile : xmlFiles) {
                                if (xmlFile.getName().endsWith("jrxml")) {
                                    files.add(xmlFile);
                                }
                            }
                        }
                        ANNOTATOR.warningsSearchMode = warningsSearchMode;
                        LSFHighlightVisitorImpl highlightVisitor = new LSFHighlightVisitorImpl(warningsSearchMode);

                        int index = 0;
                        for (VirtualFile file : files) {
                            if (FileStatusManager.getInstance(project).getStatus(file) == FileStatus.IGNORED) {
                                continue;
                            }
                            index++;
                            indicator.setText("Processing " + index + "/" + files.size() + ": " + file.getName());
                            if (file.getFileType() == LSFFileType.INSTANCE) {
                                PsiFile lsfFile = PsiManager.getInstance(project).findFile(file);
                                findLSFErrors(lsfFile);
                                if(warningsSearchMode && lsfFile != null)
                                    highlightVisitor.analyze(lsfFile);

                            } else {
                                findInjectedErrors(PsiManager.getInstance(project).findFile(file));
                            }
                            indicator.setFraction((double) index/files.size());
                        }

                        Notifications.Bus.notify(new Notification("", "", "Searching for errors finished", NotificationType.INFORMATION));
                    }
                });
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
                NotificationsConfigurationImpl.getInstanceImpl().SHOW_BALLOONS = showBalloonsState;
            }
        });
    }

    private void findLSFErrors(PsiElement element) {
        ANNOTATOR.annotate(element, new AnnotationHolderImpl(new AnnotationSession(element.getContainingFile())), true);

        for (PsiElement child : element.getChildren()) {
            findLSFErrors(child);
        }
    }

    private void findInjectedErrors(PsiElement element) {
        Set<LSFFile> files = LSFPsiUtils.collectInjectedLSFFiles(element, project);

        for (PsiElement lsfFile : files) {
            findLSFErrors(lsfFile);
        }
    }

    public static void showErrorMessage(final PsiElement element, final String errorMessage){
        showErrorMessage(element, errorMessage, LSFErrorLevel.ERROR);
    }

    public static void showErrorMessage(final PsiElement element, final String errorMessage, LSFErrorLevel errorLevel) {
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

        Module module = ModuleUtilCore.findModuleForPsiElement(element);
        if (module != null) {
            String moduleName = module.getName();
            String linkToTheElement = "<a href=\"\">(" + moduleName + ") " + file.getName() + "(" + lineNumber + ":" + linePosition + ")</a>";
            Notifications.Bus.notify(new Notification(errorLevel == LSFErrorLevel.ERROR ? "LSF errors" : "LSF warnings", linkToTheElement, errorMessage,
                    errorLevel == LSFErrorLevel.ERROR ? NotificationType.ERROR : NotificationType.WARNING, new NotificationListener.Adapter() {
                @Override
                protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent e) {
                    ((NavigationItem) element).navigate(true);
                }
            }));
        }
    }

    private class ExcludeModulesDialog extends DialogWrapper {
        private JTextField modulesToExclude;

        protected ExcludeModulesDialog() {
            super(project);
            init();
            setTitle("Find Errors Settings");
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return modulesToExclude;
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel container = new JPanel(new BorderLayout());
            
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            JLabel label = new JLabel("Excluded modules: ");

            modulesToExclude = new JTextField(propertiesComponent.getValue(EXCLUDED_MODULES));
            modulesToExclude.setColumns(30);
            panel.add(label);
            panel.add(modulesToExclude);

            container.add(panel);


            JPanel boxesPanel = new JPanel();
            boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));

            final JCheckBox includeLSFFilesBox = new JCheckBox("Search in LSF files");
            includeLSFFilesBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    includeLSFFiles = includeLSFFilesBox.isSelected();
                }
            });
            includeLSFFiles = Boolean.valueOf(propertiesComponent.getValue(INCLUDE_LSF_FILES));
            includeLSFFilesBox.setSelected(includeLSFFiles);
            boxesPanel.add(includeLSFFilesBox);

            final JCheckBox includeJavaFilesBox = new JCheckBox("Search in Java files");
            includeJavaFilesBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    includeJavaFiles = includeJavaFilesBox.isSelected();
                }
            });
            includeJavaFiles = Boolean.valueOf(propertiesComponent.getValue(INCLUDE_JAVA_FILES));
            includeJavaFilesBox.setSelected(includeJavaFiles);
            boxesPanel.add(includeJavaFilesBox);

            final JCheckBox includeJrxmlFilesBox = new JCheckBox("Search in JRXML files");
            includeJrxmlFilesBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    includeJrxmlFiles = includeJrxmlFilesBox.isSelected();
                }
            });
            includeJrxmlFiles = Boolean.valueOf(propertiesComponent.getValue(INCLUDE_JRXML_FILES));
            includeJrxmlFilesBox.setSelected(includeJrxmlFiles);
            boxesPanel.add(includeJrxmlFilesBox);

            final JCheckBox warningsSearchModeBox = new JCheckBox("Show warnings");
            warningsSearchModeBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    warningsSearchMode = warningsSearchModeBox.isSelected();
                }
            });
            warningsSearchMode = Boolean.valueOf(propertiesComponent.getValue(WARNINGS_SEARCH_MODE));
            warningsSearchModeBox.setSelected(warningsSearchMode);
            boxesPanel.add(warningsSearchModeBox);

            container.add(boxesPanel, BorderLayout.SOUTH);

            return container;
        }

        @Override
        protected void doOKAction() {
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
            propertiesComponent.setValue(EXCLUDED_MODULES, modulesToExclude.getText());
            propertiesComponent.setValue(INCLUDE_LSF_FILES, String.valueOf(includeLSFFiles));
            propertiesComponent.setValue(INCLUDE_JAVA_FILES, String.valueOf(includeJavaFiles));
            propertiesComponent.setValue(INCLUDE_JRXML_FILES, String.valueOf(includeJrxmlFiles));
            propertiesComponent.setValue(WARNINGS_SEARCH_MODE, String.valueOf(warningsSearchMode));

            StringTokenizer tokenizer = new StringTokenizer(modulesToExclude.getText(), ",;");
            final List<String> modules = new ArrayList<>();
            while (tokenizer.hasMoreElements()) {
                modules.add(tokenizer.nextToken().trim());
            }
            super.doOKAction();
            executeSearchTask(modules);
        }
    }
}
