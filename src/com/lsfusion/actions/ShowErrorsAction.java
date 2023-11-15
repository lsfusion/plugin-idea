package com.lsfusion.actions;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.ide.util.PropertiesComponent;
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
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Segment;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.inspections.LSFProblemsVisitor;
import com.lsfusion.lang.LSFErrorLevel;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.util.LSFFileUtils;
import com.lsfusion.util.LSFPsiUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class ShowErrorsAction extends AnAction {
    private final String INCLUDED_MODULES = "INCLUDED_MODULES_ERRORS_SEARCH";
    private final String INCLUDE_LSF_FILES = "INCLUDE_LSF_FILES_ERRORS_SEARCH";
    private final String INCLUDE_JAVA_FILES = "INCLUDE_JAVA_FILES_ERRORS_SEARCH";
    private final String INCLUDE_JRXML_FILES = "INCLUDE_JRXML_FILES_ERRORS_SEARCH";
    private final String WARNINGS_SEARCH_MODE = "WARNINGS_SEARCH_MODE";
    private final LSFReferenceAnnotator ANNOTATOR = new LSFReferenceAnnotator();

    private Project project;
    PropertiesComponent propertiesComponent;
    private boolean includeLSFFiles = true;
    private boolean includeJavaFiles = false;
    private boolean includeJrxmlFiles = false;
    private boolean warningsSearchMode = false;

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        project = getEventProject(e);
        if(project != null) {
            propertiesComponent = PropertiesComponent.getInstance(project);
            boolean enabledMeta = MetaChangeDetector.getInstance(project).getMetaEnabled();
            if (!enabledMeta) {
                if(JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                        "Meta code is disabled. You must enable meta before showing errors", "Errors search",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    new MetaCodeEnableAction().actionPerformed(e);
                    enabledMeta = MetaChangeDetector.getInstance(project).getMetaEnabled();
                }
            }
            if(enabledMeta) {
                ShowErrorsDialog dialog = new ShowErrorsDialog(LSFFileUtils.getModules(project));
                dialog.show();
            }
        }
    }

    private void executeSearchTask(final List<String> includedModules) {
        final boolean showBalloonsState = NotificationsConfigurationImpl.getInstanceImpl().SHOW_BALLOONS;
        NotificationsConfigurationImpl.getInstanceImpl().SHOW_BALLOONS = false;

        final Progressive progress = indicator -> ApplicationManager.getApplication().runReadAction(() -> {
            Notifications.Bus.notify(new Notification("", "", "Searching for errors started", NotificationType.INFORMATION));

            GlobalSearchScope searchScope = LSFFileUtils.getScope(includedModules, project);

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
            ANNOTATOR.errorsSearchMode = true;
            ANNOTATOR.warningsSearchMode = warningsSearchMode;

            int index = 0;
            for (VirtualFile file : files) {
                if (FileStatusManager.getInstance(project).getStatus(file) == FileStatus.IGNORED) {
                    continue;
                }
                index++;
                indicator.setText("Processing " + index + "/" + files.size() + ": " + file.getName());
                if (file.getFileType() == LSFFileType.INSTANCE) {
                    PsiFile lsfFile = PsiManager.getInstance(project).findFile(file);
                    if(lsfFile != null) {
                        findLSFErrors(lsfFile);
                        if (warningsSearchMode) {
                            LSFProblemsVisitor.analyze(lsfFile);
                        }
                    }

                } else {
                    findInjectedErrors(PsiManager.getInstance(project).findFile(file));
                }
                indicator.setFraction((double) index/files.size());
            }

            Notifications.Bus.notify(new Notification("", "", "Searching for errors finished", NotificationType.INFORMATION));
        });

        Task task = new Task.Modal(project, "Searching for errors", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                progress.run(indicator);
            }
        };

        ProgressManager.getInstance().run(task);

        // todo: Notifications tool window was created in 2021.3. 
        //  ID should be replaced with NotificationsToolWindowFactory.ID as soon as supported versions range will allow to.
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notifications");
        if (toolWindow == null) {
            toolWindow = EventLog.getEventLog(project);
        }
        if (toolWindow != null && !toolWindow.isVisible()) {
            toolWindow.activate(null);
        }
        
        ApplicationManager.getApplication().invokeLater(() -> NotificationsConfigurationImpl.getInstanceImpl().SHOW_BALLOONS = showBalloonsState);
    }

    private void findLSFErrors(PsiElement element) {
        ANNOTATOR.annotate(element, null);

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

    public static void showErrorMessage(final PsiElement element, final String errorMessage, LSFErrorLevel errorLevel) {
        final PsiFile file = element.getContainingFile();
        final Document document = PsiDocumentManager.getInstance(element.getProject()).getDocument(file);
        final SmartPsiElementPointer<PsiElement> pointer = SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);
        final Segment range = pointer.getRange();
        int lineNumber = -1;
        int linePosition = -1;
        if (document != null && range != null) {
            lineNumber = document.getLineNumber(range.getStartOffset()) + 1;
            linePosition = range.getStartOffset() - document.getLineStartOffset(lineNumber - 1) + 1;
        }

        Module module = ModuleUtilCore.findModuleForPsiElement(element);
        if (module != null) {
            Notifications.Bus.notify(new Notification(
                            errorLevel == LSFErrorLevel.ERROR ? "LSF errors" : "LSF warnings",
                            "Error in " + module.getName() + " module:",
                            errorMessage,
                            errorLevel == LSFErrorLevel.ERROR ? NotificationType.ERROR : NotificationType.WARNING
                    ).addAction(
                            NotificationAction.create(
                                    file.getName() + "(" + lineNumber + ":" + linePosition + ")",
                                    (event, inotification) -> ((NavigationItem) element).navigate(true)
                            )
                    )
            );
        }
    }

    private class ShowErrorsDialog extends DialogWrapper {
        Module[] modules;
        CheckBoxGroup modulesCheckBoxGroup;

        protected ShowErrorsDialog(Module[] modules) {
            super(project);
            this.modules = modules;
            init();
            setTitle("Find Errors Settings");
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel container = new JPanel(new BorderLayout());

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            JPanel boxesPanel = new JPanel();
            boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.Y_AXIS));

            final JCheckBox includeLSFFilesBox = new JCheckBox("Search in LSF files");
            includeLSFFilesBox.addActionListener(e -> includeLSFFiles = includeLSFFilesBox.isSelected());
            includeLSFFiles = Boolean.parseBoolean(propertiesComponent.getValue(INCLUDE_LSF_FILES));
            includeLSFFilesBox.setSelected(includeLSFFiles);
            boxesPanel.add(includeLSFFilesBox);

            final JCheckBox includeJavaFilesBox = new JCheckBox("Search in Java files");
            includeJavaFilesBox.addActionListener(e -> includeJavaFiles = includeJavaFilesBox.isSelected());
            includeJavaFiles = Boolean.parseBoolean(propertiesComponent.getValue(INCLUDE_JAVA_FILES));
            includeJavaFilesBox.setSelected(includeJavaFiles);
            boxesPanel.add(includeJavaFilesBox);

            final JCheckBox includeJrxmlFilesBox = new JCheckBox("Search in JRXML files");
            includeJrxmlFilesBox.addActionListener(e -> includeJrxmlFiles = includeJrxmlFilesBox.isSelected());
            includeJrxmlFiles = Boolean.parseBoolean(propertiesComponent.getValue(INCLUDE_JRXML_FILES));
            includeJrxmlFilesBox.setSelected(includeJrxmlFiles);
            boxesPanel.add(includeJrxmlFilesBox);

            final JCheckBox warningsSearchModeBox = new JCheckBox("Show warnings");
            warningsSearchModeBox.addActionListener(e -> warningsSearchMode = warningsSearchModeBox.isSelected());
            warningsSearchMode = Boolean.parseBoolean(propertiesComponent.getValue(WARNINGS_SEARCH_MODE));
            warningsSearchModeBox.setSelected(warningsSearchMode);
            boxesPanel.add(warningsSearchModeBox);

            container.add(boxesPanel);

            if(modules.length > 1) {
                modulesCheckBoxGroup = new CheckBoxGroup(modules, getIncludedModules());
                panel.add(modulesCheckBoxGroup);
            }
            container.add(panel, BorderLayout.SOUTH);

            return container;
        }

        @Override
        protected void doOKAction() {
            List<String> includedModules = modulesCheckBoxGroup != null ? modulesCheckBoxGroup.getIncludedModules() : new ArrayList<>();
            setIncludedModules(includedModules);

            propertiesComponent.setValue(INCLUDE_LSF_FILES, String.valueOf(includeLSFFiles));
            propertiesComponent.setValue(INCLUDE_JAVA_FILES, String.valueOf(includeJavaFiles));
            propertiesComponent.setValue(INCLUDE_JRXML_FILES, String.valueOf(includeJrxmlFiles));
            propertiesComponent.setValue(WARNINGS_SEARCH_MODE, String.valueOf(warningsSearchMode));

            super.doOKAction();
            executeSearchTask(includedModules.size() == modules.length ? new ArrayList<>() : includedModules);
        }

        private List<String> getIncludedModules() {
            String includedModules = propertiesComponent.getValue(INCLUDED_MODULES);
            return includedModules != null && !includedModules.isEmpty() ? Arrays.asList(includedModules.split(",")) : new ArrayList<>();
        }

        private void setIncludedModules(List<String> includedModules) {
            propertiesComponent.setValue(INCLUDED_MODULES, StringUtils.join(includedModules, ","));
        }
    }
}
