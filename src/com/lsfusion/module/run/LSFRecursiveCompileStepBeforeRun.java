package com.lsfusion.module.run;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileWithCompileBeforeLaunchOption;
import com.intellij.execution.process.ScriptRunnerUtil;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.concurrency.Semaphore;
import com.lsfusion.LSFIcons;
import org.jdesktop.swingx.util.Utilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;

import static com.intellij.execution.process.ScriptRunnerUtil.STDOUT_OUTPUT_KEY_FILTER;

public class LSFRecursiveCompileStepBeforeRun extends BeforeRunTaskProvider<LSFRecursiveCompileStepBeforeRun.RecursiveCompileBeforeRunTask> {
    public static final Key<LSFRecursiveCompileStepBeforeRun.RecursiveCompileBeforeRunTask> ID = Key.create("Recursive compile");

    private final int TIMEOUT = 100000;
    private final Project myProject;
    private static final Logger LOG = Logger.getInstance(Task.class);

    public LSFRecursiveCompileStepBeforeRun(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public Key<RecursiveCompileBeforeRunTask> getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Recursive compile";
    }

    @Override
    public @Nullable Icon getIcon() {
        return LSFIcons.RUN;
    }

    @Override
    public @Nullable Icon getTaskIcon(RecursiveCompileBeforeRunTask task) {
        return getIcon();
    }

    @Override
    public @Nullable RecursiveCompileBeforeRunTask createTask(@NotNull RunConfiguration runConfiguration) {
        RecursiveCompileBeforeRunTask task = new RecursiveCompileBeforeRunTask();
        task.setEnabled(true);
        return task;
    }

    @Override
    public boolean executeTask(@NotNull DataContext context, @NotNull RunConfiguration configuration, @NotNull ExecutionEnvironment environment, @NotNull RecursiveCompileBeforeRunTask task) {
        final Module[] modules = ((RunProfileWithCompileBeforeLaunchOption) configuration).getModules();
        final boolean[] canceled = {false};
        if (modules.length > 0) {
            Module module = modules[0]; // expecting single module here
            String exe = Utilities.isWindows() ? "cmd" : "/bin/sh";
            String exec = Utilities.isWindows() ? "/c" : "-c";

            GeneralCommandLine generalCommandLine = new GeneralCommandLine(exe, exec, "mvn", "--pl", ":" + module.getName(), "--am", "compile");
            generalCommandLine.setCharset(StandardCharsets.UTF_8);
            generalCommandLine.setWorkDirectory(myProject.getBasePath());
            generalCommandLine.setRedirectErrorStream(true);

            final Semaphore compileDone = new Semaphore();
            compileDone.down();

            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    if (myProject.isDisposed()) {
                        compileDone.up();
                    } else {
                        FileDocumentManager.getInstance().saveAllDocuments();
                        
                        String moduleRCText = "'" + module.getName() + "' recursive compile";
                        ProgressManager.getInstance().run(new Task.Backgroundable(myProject, "Performing " + moduleRCText, false) {
                            public void run(final @NotNull ProgressIndicator indicator) {
                                String commandLineOutputStr = null;
                                try {
                                    commandLineOutputStr = ScriptRunnerUtil.getProcessOutput(generalCommandLine, STDOUT_OUTPUT_KEY_FILTER, TIMEOUT);
                                } catch (ExecutionException e) {
                                    compileDone.up();
                                    LOG.error(e);
                                }
                                String finalCommandLineOutputStr = commandLineOutputStr;

                                if (commandLineOutputStr != null && commandLineOutputStr.contains("[ERROR]")) {
                                    Notification notification = NotificationGroupManager.getInstance().getNotificationGroup("LSF Recursive Compile").createNotification(
                                            moduleRCText + " failed",
                                            NotificationType.ERROR);
                                    Notification action = notification.addAction(NotificationAction.create("Details...", anActionEvent -> {
                                        RecursiveCompileErrorDialog.showDialog(myProject, moduleRCText + " log", finalCommandLineOutputStr);
                                        notification.expire();
                                    }));
                                    action.notify(myProject);
                                    canceled[0] = true;
                                }
                                compileDone.up();
                            }
                        });
                    }
                } catch (Throwable e) {
                    compileDone.up();
                    LOG.error(e);
                }
            });

            compileDone.waitFor();
        }
        
        return !canceled[0];
    }

    public static class RecursiveCompileBeforeRunTask extends BeforeRunTask<RecursiveCompileBeforeRunTask> {
        public RecursiveCompileBeforeRunTask() {
            super(ID);
            setEnabled(true);
        }
    }

    public static final class RecursiveCompileErrorDialog extends DialogWrapper {
        private final JBScrollPane myContentPanel;
        private final JBTextArea textArea;

        private RecursiveCompileErrorDialog(String title,
                                            Project project,
                                            String error) {
            super(project, true);
            setTitle(title);

            final boolean[] initialPaint = {true};
            textArea = new JBTextArea(error) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (initialPaint[0]) {
                        scrollToEnd();
                        initialPaint[0] = false;
                    }
                }
            };
            textArea.setEditable(false);

            myContentPanel = new JBScrollPane(textArea) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(850, 500);
                }
            };
            init();
        }

        private void scrollToEnd() {
            JScrollBar verticalScrollBar = myContentPanel.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        }

        public static void showDialog(@NotNull Project project,
                                      @NotNull String title,
                                      @NotNull String error) {
            new RecursiveCompileErrorDialog(title, project, error).show();
        }

        @Override
        protected JComponent createCenterPanel() {
            return myContentPanel;
        }
    }
}
