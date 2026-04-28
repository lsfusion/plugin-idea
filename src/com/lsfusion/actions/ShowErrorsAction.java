package com.lsfusion.actions;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.navigation.NavigationItem;
import com.intellij.notification.*;
import com.intellij.notification.impl.NotificationsConfigurationImpl;
import com.intellij.notification.impl.NotificationsToolWindowFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Progressive;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Segment;
import com.intellij.openapi.util.TextRange;
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
import com.lsfusion.lang.psi.LSFGlobalResolver;
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
                    ActionUtil.performActionDumbAwareWithCallbacks(new MetaCodeEnableAction(), e);
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

        final Progressive progress = indicator -> {
            Notifications.Bus.notify(new Notification("", "", "Searching for errors started", NotificationType.INFORMATION));

            ANNOTATOR.errorsSearchMode = true;
            ANNOTATOR.warningsSearchMode = warningsSearchMode;

            List<VirtualFile> files = ApplicationManager.getApplication().runReadAction((Computable<List<VirtualFile>>) () -> collectFiles(ApplicationManager.getApplication().runReadAction((Computable<GlobalSearchScope>) () ->
                    LSFFileUtils.getScope(includedModules, project, GlobalSearchScope.projectScope(project)))));

            int index = 0;
            for (VirtualFile file : files) {
                if (FileStatusManager.getInstance(project).getStatus(file) == FileStatus.IGNORED) {
                    continue;
                }
                index++;
                indicator.setText("Processing " + index + "/" + files.size() + ": " + file.getName());
                processFile(file);
                indicator.setFraction((double) index / files.size());
            }

            Notifications.Bus.notify(new Notification("", "", "Searching for errors finished", NotificationType.INFORMATION));
        };

        Task task = new Task.Modal(project, "Searching for errors", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                progress.run(indicator);
            }
        };

        ProgressManager.getInstance().run(task);

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(NotificationsToolWindowFactory.ID);
        if (toolWindow == null) {
            toolWindow = EventLog.getEventLog(project);
        }
        if (toolWindow != null && !toolWindow.isVisible()) {
            toolWindow.activate(null);
        }
        
        ApplicationManager.getApplication().invokeLater(() -> NotificationsConfigurationImpl.getInstanceImpl().SHOW_BALLOONS = showBalloonsState);
    }

    private List<VirtualFile> collectFiles(GlobalSearchScope searchScope) {
        List<VirtualFile> files = new ArrayList<>();

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

        return files;
    }

    private void processFile(VirtualFile virtualFile) {
        PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> PsiManager.getInstance(project).findFile(virtualFile));
        if (virtualFile.getFileType() == LSFFileType.INSTANCE) {
            Set<ReportedError> initialErrors = collectReportedErrors(psiFile);
            if (!initialErrors.isEmpty()) {
                //second param false -> do not include opened in editor files
                ApplicationManager.getApplication().invokeAndWait(() -> PsiDocumentManager.getInstance(project).reparseFiles(Collections.singleton(psiFile.getVirtualFile()), false));
                Set<ReportedError> confirmedErrors = collectReportedErrors(ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> PsiManager.getInstance(project).findFile(psiFile.getVirtualFile())));
                for (ReportedError error : confirmedErrors) {
                    showErrorMessage(error.file, error.startOffset, error.text, error.level, ModuleUtilCore.findModuleForFile(error.file.getVirtualFile(),
                                    error.file.getProject()),
                            () -> new OpenFileDescriptor(error.file.getProject(), error.file.getVirtualFile(), Math.max(error.startOffset, 0)).navigate(true));
                }
            }
            if (warningsSearchMode) {
                DumbService.getInstance(project).runReadActionInSmartMode(() -> LSFProblemsVisitor.analyze(psiFile));
            }
            return;
        }

        DumbService.getInstance(project).runReadActionInSmartMode(() -> findInjectedErrors(psiFile));
    }

    private void findLSFErrors(PsiElement element) {
        showSyntaxError(element);
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

    private Set<ReportedError> collectReportedErrors(PsiFile file) {
        Set<ReportedError> errors = new LinkedHashSet<>();
        LSFReferenceAnnotator confirmAnnotator = new LSFReferenceAnnotator();
        confirmAnnotator.errorsSearchMode = true;
        confirmAnnotator.warningsSearchMode = warningsSearchMode;
        confirmAnnotator.searchMessageConsumer = (element, text, level) -> errors.add(ReportedError.from(file, element, text, level));
        DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            collectSyntaxErrors(file, errors);
            collectFileErrors(file, confirmAnnotator);
        });
        return errors;
    }

    private void collectSyntaxErrors(PsiElement element, Set<ReportedError> errors) {
        if (element instanceof PsiErrorElement && !LSFReferenceAnnotator.isInMetaDecl(element)) {
            errors.add(ReportedError.from(element.getContainingFile(), element, getSyntaxErrorText((PsiErrorElement) element), LSFErrorLevel.ERROR));
        }

        for (PsiElement child : element.getChildren()) {
            collectSyntaxErrors(child, errors);
        }
    }

    private void collectFileErrors(PsiElement element, LSFReferenceAnnotator annotator) {
        annotator.annotate(element, null);
        for (PsiElement child : element.getChildren()) {
            collectFileErrors(child, annotator);
        }
    }

    private void showSyntaxError(PsiElement element) {
        if (element instanceof PsiErrorElement) {
            showErrorMessage(element, getSyntaxErrorText((PsiErrorElement) element), LSFErrorLevel.ERROR);
        }
    }

    private static String getSyntaxErrorText(PsiErrorElement errorElement) {
        String description = errorElement.getErrorDescription();
        return description.isEmpty() ? "Syntax error" : description;
    }

    public static void showErrorMessage(final PsiElement element, final String errorMessage, LSFErrorLevel errorLevel) {
        final Segment range = SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element).getRange();
        int startOffset = range != null ? range.getStartOffset() : -1;
        showErrorMessage(element.getContainingFile(), startOffset, errorMessage, errorLevel, ModuleUtilCore.findModuleForPsiElement(element), () -> navigate(element, startOffset));
    }

    private static void showErrorMessage(PsiFile file, int offset, String errorMessage, LSFErrorLevel errorLevel,
                                         Module module, Runnable navigateAction) {
        final Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        int lineNumber = -1;
        int linePosition = -1;
        if (document != null && offset >= 0 && offset <= document.getTextLength()) {
            lineNumber = document.getLineNumber(offset) + 1;
            linePosition = offset - document.getLineStartOffset(lineNumber - 1) + 1;
        }

        if (module != null) {
            Notifications.Bus.notify(new Notification(
                            errorLevel == LSFErrorLevel.ERROR ? "LSF errors" : "LSF warnings",
                            "Error in " + module.getName() + " module:",
                            errorMessage,
                            errorLevel == LSFErrorLevel.ERROR ? NotificationType.ERROR : NotificationType.WARNING
                    ).addAction(
                            NotificationAction.create(
                                    file.getName() + "(" + lineNumber + ":" + linePosition + ")",
                                    (event, inotification) -> navigateAction.run()
                            )
                    )
            );
        }
    }

    private static void navigate(PsiElement element, int offset) {
        if (element instanceof NavigationItem) {
            ((NavigationItem) element).navigate(true);
            return;
        }

        PsiFile file = element.getContainingFile();
        VirtualFile virtualFile = file != null ? file.getVirtualFile() : null;
        if (virtualFile != null) {
            new OpenFileDescriptor(file.getProject(), virtualFile, Math.max(offset, 0)).navigate(true);
        }
    }

    private static class ReportedError {
        private final PsiFile file;
        private final Object fileKey;
        private final int startOffset;
        private final int endOffset;
        private final String text;
        private final LSFErrorLevel level;

        private ReportedError(PsiFile file, Object fileKey, int startOffset, int endOffset, String text, LSFErrorLevel level) {
            this.file = file;
            this.fileKey = fileKey;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.text = text;
            this.level = level;
        }

        private static ReportedError from(PsiFile file, PsiElement element, String text, LSFErrorLevel level) {
            TextRange range = element.getTextRange();
            return new ReportedError(file, file.getVirtualFile() != null ? file.getVirtualFile() : file,
                    range != null ? range.getStartOffset() : -1, range != null ? range.getEndOffset() : -1, text, level);
        }

        @Override
        public boolean equals(Object o) {
            return this == o || (o instanceof ReportedError && startOffset == ((ReportedError) o).startOffset && endOffset == ((ReportedError) o).endOffset
                    && Objects.equals(fileKey, ((ReportedError) o).fileKey) && Objects.equals(text, ((ReportedError) o).text) && level == ((ReportedError) o).level);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fileKey, startOffset, endOffset, text, level);
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
