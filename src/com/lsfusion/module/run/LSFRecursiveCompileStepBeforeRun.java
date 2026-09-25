package com.lsfusion.module.run;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileWithCompileBeforeLaunchOption;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.impl.RunConfigurationBeforeRunProvider;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.lsfusion.LSFIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.util.List;

/**
 * "Recursive compile" before-launch step: {@code mvn compile --also-make} for the run configuration's module.
 * Same mechanics as the bundled "Run Maven Goal" step (a temporary Maven run configuration in the Run tool window,
 * so the build is visible, can be stopped, and honors the IDE's Maven settings), except that the module is taken
 * from the run configuration instead of being picked by hand for every configuration.
 */
public class LSFRecursiveCompileStepBeforeRun extends BeforeRunTaskProvider<LSFRecursiveCompileStepBeforeRun.RecursiveCompileBeforeRunTask> {
    public static final Key<RecursiveCompileBeforeRunTask> ID = Key.create("Recursive compile");

    private final Project myProject;

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
        if (!(runConfiguration instanceof RunProfileWithCompileBeforeLaunchOption)) {
            return null;
        }
        RecursiveCompileBeforeRunTask task = new RecursiveCompileBeforeRunTask();
        task.setEnabled(false);
        return task;
    }

    @Override
    public boolean executeTask(@NotNull DataContext context, @NotNull RunConfiguration configuration, @NotNull ExecutionEnvironment env, @NotNull RecursiveCompileBeforeRunTask task) {
        Module[] modules = ((RunProfileWithCompileBeforeLaunchOption) configuration).getModules();
        if (modules.length == 0) {
            return true;
        }
        Module module = modules[0];

        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(myProject);
        MavenProject mavenProject = ApplicationManager.getApplication().runReadAction((Computable<MavenProject>) () -> projectsManager.findProject(module));
        if (mavenProject == null) {
            ExecutionUtil.handleExecutionError(myProject, env.getExecutor().getToolWindowId(), configuration.getName(),
                    new ExecutionException("Module '" + module.getName() + "' is not a Maven module, nothing to compile recursively"));
            return false;
        }

        // --also-make only picks up reactor dependencies when Maven is started from the aggregator root
        MavenProject rootProject = projectsManager.findRootProject(mavenProject);
        MavenId mavenId = mavenProject.getMavenId();
        MavenRunnerParameters params = new MavenRunnerParameters(true, rootProject.getDirectoryFile().getPath(), rootProject.getFile().getName(),
                List.of("compile"), projectsManager.getExplicitProfiles());
        params.setProjectsCmdOptionValues(List.of(mavenId.getGroupId() + ":" + mavenId.getArtifactId()));
        params.setCmdOptions("--also-make");

        ApplicationManager.getApplication().invokeAndWait(() -> FileDocumentManager.getInstance().saveAllDocuments());

        RunnerAndConfigurationSettings settings = MavenRunConfigurationType.createRunnerAndConfigurationSettings(null, null, params, myProject,
                module.getName() + " [recursive compile]", false);
        ProgramRunner<?> runner = DefaultJavaProgramRunner.getInstance();
        Executor executor = DefaultRunExecutor.getRunExecutorInstance();
        ExecutionEnvironment mavenEnv = new ExecutionEnvironment(executor, runner, settings, myProject);
        mavenEnv.setExecutionId(env.getExecutionId());
        if (!RunConfigurationBeforeRunProvider.doRunTask(executor.getId(), mavenEnv, runner)) {
            return false;
        }

        // The server's classpath takes only the output directories the VFS already knows (OrderRootsEnumerator.collectPaths
        // goes through VirtualFiles), and Maven has just recreated them behind its back: right after a clean the file
        // watcher's asynchronous refresh can lose the race to the launch, and the server starts without the module classes.
        String[] outputUrls = ApplicationManager.getApplication().runReadAction((Computable<String[]>) () ->
                OrderEnumerator.orderEntries(module).recursively().withoutSdk().withoutLibraries().classes().getUrls());
        for (String url : outputUrls) {
            VirtualFileManager.getInstance().refreshAndFindFileByUrl(url);
        }
        return true;
    }

    public static class RecursiveCompileBeforeRunTask extends BeforeRunTask<RecursiveCompileBeforeRunTask> {
        public RecursiveCompileBeforeRunTask() {
            super(ID);
            setEnabled(true);
        }
    }
}
