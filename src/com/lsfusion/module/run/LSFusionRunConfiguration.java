package com.lsfusion.module.run;

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.*;
import com.intellij.execution.configuration.AbstractRunConfiguration;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.lsfusion.debug.LSFDebuggerRunner.LIGHT_START_PROPERTY;
import static com.lsfusion.debug.LSFDebuggerRunner.PLUGIN_ENABLED_PROPERTY;
import static com.lsfusion.module.LSFusionModuleBuilder.BOOTSTRAP_CLASS_NAME;

public class LSFusionRunConfiguration extends AbstractRunConfiguration implements CommonJavaRunConfigurationParameters {//}, RunConfigurationWithSuppressedDefaultDebugAction {

    public static final String MAIN_CLASS_NAME = BOOTSTRAP_CLASS_NAME;

    private static final int DEFAULT_DEBUGGER_PORT = 1299;

    public String VM_PARAMETERS;
    public String PROGRAM_PARAMETERS;
    public String WORKING_DIRECTORY;
    public boolean ALTERNATIVE_JRE_PATH_ENABLED;
    public String ALTERNATIVE_JRE_PATH;
    public boolean LIGHT_START;

    public boolean PASS_PARENT_ENVS = true;
    private Map<String, String> myEnvs = new LinkedHashMap<>();

    protected LSFusionRunConfiguration(final String name, final Project project, final ConfigurationFactory factory) {
        super(name, new JavaRunConfigurationModule(project, true), factory);
    }

    protected LSFusionRunConfiguration(final String name, final Project project, final ConfigurationFactory factory, boolean lightStart) {
        super(name, new JavaRunConfigurationModule(project, true), factory);
        this.LIGHT_START = lightStart;
    }

    @Override
    public JavaRunConfigurationModule getConfigurationModule() {
        return (JavaRunConfigurationModule) super.getConfigurationModule();
    }

    public RunProfileState getState(@NotNull final Executor executor, @NotNull final ExecutionEnvironment env) throws ExecutionException {
        final JavaCommandLineState state = new LSFServerCommandLineState(this, env, LIGHT_START);
        JavaRunConfigurationModule module = getConfigurationModule();
        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject(), module.getSearchScope()));
        return state;
    }

    @NotNull
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        SettingsEditorGroup<LSFusionRunConfiguration> group = new SettingsEditorGroup<>();
        group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new LSFusionRunConfigurationEditor(getProject()));
        JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
        group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<>());
        return group;
    }

    public void checkConfiguration() throws RuntimeConfigurationException {
        JavaParametersUtil.checkAlternativeJRE(this);
        ProgramParametersUtil.checkWorkingDirectoryExist(this, getProject(), getConfigurationModule().getModule());
        JavaRunConfigurationExtensionManager.checkConfigurationIsValid(this);
    }

    public void setVMParameters(String value) {
        VM_PARAMETERS = value;
    }

    public String getVMParameters() {
        return VM_PARAMETERS != null ? VM_PARAMETERS : "-Xmx1200m -ea";
    }

    public void setProgramParameters(String value) {
        PROGRAM_PARAMETERS = value;
    }

    public String getProgramParameters() {
        return PROGRAM_PARAMETERS;
    }

    public void setWorkingDirectory(String value) {
        WORKING_DIRECTORY = ExternalizablePath.urlValue(value);
    }

    public String getWorkingDirectory() {
        return WORKING_DIRECTORY != null ? ExternalizablePath.localPathValue(WORKING_DIRECTORY) : "$MODULE_DIR$";
    }

    public void setPassParentEnvs(boolean passParentEnvs) {
        PASS_PARENT_ENVS = passParentEnvs;
    }

    @NotNull
    public Map<String, String> getEnvs() {
        return myEnvs;
    }

    public void setEnvs(@NotNull final Map<String, String> envs) {
        myEnvs.clear();
        myEnvs.putAll(envs);
    }

    public boolean isPassParentEnvs() {
        return PASS_PARENT_ENVS;
    }

    @Nullable
    public String getRunClass() {
        return null;
    }

    @Nullable
    public String getPackage() {
        return null;
    }

    public boolean isAlternativeJrePathEnabled() {
        return ALTERNATIVE_JRE_PATH_ENABLED;
    }

    public void setAlternativeJrePathEnabled(boolean enabled) {
        this.ALTERNATIVE_JRE_PATH_ENABLED = enabled;
    }

    public String getAlternativeJrePath() {
        return ALTERNATIVE_JRE_PATH;
    }

    public void setAlternativeJrePath(String path) {
        this.ALTERNATIVE_JRE_PATH = path;
    }

    public Collection<Module> getValidModules() {
        return JavaRunConfigurationModule.getModulesForClass(getProject(), MAIN_CLASS_NAME);
    }

    public void readExternal(final Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        JavaRunConfigurationExtensionManager.getInstance().readExternal(this, element);
        DefaultJDOMExternalizer.readExternal(this, element);
        readModule(element);
        EnvironmentVariablesComponent.readExternal(element, getEnvs());
    }

    public void writeExternal(final Element element) throws WriteExternalException {
        super.writeExternal(element);
        JavaRunConfigurationExtensionManager.getInstance().writeExternal(this, element);
        DefaultJDOMExternalizer.writeExternal(this, element);
        writeModule(element);
        EnvironmentVariablesComponent.writeExternal(element, getEnvs());
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    @Override
    protected ModuleBasedConfiguration createInstance() {
        return new LSFusionRunConfiguration(getName(), getProject(), LSFusionRunConfigurationType.getInstance().getConfigurationFactory());
    }
    
    public int getDebuggerPort() {
        String vmParams = getVMParameters();
        String[] strings = vmParams.split(" ");
        for (String s : strings) {
            if (s.startsWith("-Dlsfusion.debugger.port")) {
                Integer result = Integer.valueOf(s.substring(s.lastIndexOf("=") + 1));
                if (result != null) {
                    return result;
                }
            }
        }
        return DEFAULT_DEBUGGER_PORT;
    }

    public static class LSFServerCommandLineState extends JavaCommandLineState {

        private final LSFusionRunConfiguration myConfiguration;
        private final boolean lightStart;

        public LSFServerCommandLineState(@NotNull final LSFusionRunConfiguration configuration, final ExecutionEnvironment environment, final boolean lightStart) {
            super(environment);
            myConfiguration = configuration;
            this.lightStart = lightStart;

        }

        protected JavaParameters createJavaParameters() throws ExecutionException {
            final JavaParameters params = new JavaParameters();
            final JavaRunConfigurationModule module = myConfiguration.getConfigurationModule();

            final int classPathType = JavaParametersUtil.getClasspathType(module, MAIN_CLASS_NAME, false);
            final String jreHome = myConfiguration.ALTERNATIVE_JRE_PATH_ENABLED ? myConfiguration.ALTERNATIVE_JRE_PATH : null;
            JavaParametersUtil.configureModule(module, params, classPathType, jreHome);
            JavaParametersUtil.configureConfiguration(params, myConfiguration);
            params.getVMParametersList().addProperty(LIGHT_START_PROPERTY, String.valueOf(lightStart));

            if (!params.getVMParametersList().hasProperty(PLUGIN_ENABLED_PROPERTY)) {
                params.getVMParametersList().addProperty(PLUGIN_ENABLED_PROPERTY, "true");
            }

            params.setMainClass(MAIN_CLASS_NAME);
            for (RunConfigurationExtension ext : Extensions.getExtensions(RunConfigurationExtension.EP_NAME)) {
                ext.updateJavaParameters(myConfiguration, params, getRunnerSettings());
            }

            return params;
        }

        @NotNull
        @Override
        protected OSProcessHandler startProcess() throws ExecutionException {
            final OSProcessHandler handler = super.startProcess();
            RunnerSettings runnerSettings = getRunnerSettings();
            JavaRunConfigurationExtensionManager.getInstance().attachExtensionsToProcess(myConfiguration, handler, runnerSettings);
            return handler;
        }
    }
}
