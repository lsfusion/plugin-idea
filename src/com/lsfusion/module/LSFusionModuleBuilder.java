package com.lsfusion.module;

import com.intellij.CommonBundle;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.lsfusion.LSFBundle;
import com.lsfusion.LSFIcons;
import com.lsfusion.module.run.LSFusionRunConfiguration;
import com.lsfusion.module.run.LSFusionRunConfigurationType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaResourceRootType;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.lsfusion.module.LSFusionTemplates.TEMPLATE_LSFUSION_MODULE;
import static com.lsfusion.module.LSFusionTemplates.TEMPLATE_LSFUSION_SETTINGS;

public class LSFusionModuleBuilder extends ModuleBuilder {
    public static final String BOOTSTRAP_CLASS_NAME = "lsfusion.server.logics.BusinessLogicsBootstrap";

    public static final String BUILDER_ID = "lsfusion";
    public static final String BUILDER_NAME = LSFBundle.message("lsf.module.type.name");
    public static final String BUILDER_DESCRIPTION = LSFBundle.message("lsf.module.type.description");

    private static final String SETTINGS_FILE = "conf/settings.properties";
    private static final String MAIN_LSF_NAME = "Main";
    private static final String MAIN_LSF_FILE = MAIN_LSF_NAME + ".lsf";

    private boolean createSettingsFile;

    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String serverPort;
    private String initialAdminPassword;

    @Nullable
    @Override
    public String getBuilderId() {
        return BUILDER_ID;
    }

    @Override
    public String getPresentableName() {
        return BUILDER_NAME;
    }

    @NotNull
    public String getDescription() {
        return BUILDER_DESCRIPTION;
    }

    public Icon getBigIcon() {
        return LSFIcons.MODULE_2X;
    }

    @Override
    public Icon getNodeIcon() {
        return LSFIcons.MODULE;
    }

    @Override
    public String getGroupName() {
        return "lsFusion";
    }

    public void setCreateSettingsFile(boolean createSettingsFile) {
        this.createSettingsFile = createSettingsFile;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public void setInitialAdminPassword(String initialAdminPassword) {
        this.initialAdminPassword = initialAdminPassword;
    }

    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep) {
        return new LSFusionLibraryProjectTypeStep(this, settingsStep);
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return new LSFusionLibrarySettingsStep(this, settingsStep);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        super.setupRootModel(rootModel);

        Project project = rootModel.getProject();
        final String contentEntryPath = getContentEntryPath();
        
        if (contentEntryPath != null) {
            if (createSettingsFile) {
                File settingsFile = new File(contentEntryPath, SETTINGS_FILE);
                if (!settingsFile.isDirectory() && settingsFile.getParentFile().mkdirs()) {
                    Properties properties = new Properties();
                    properties.put("DB_SERVER", dbPort == null || dbPort.trim().isEmpty() ? dbHost : dbHost + ":" + dbPort);
                    properties.put("DB_NAME", dbName);
                    properties.put("DB_USER", dbUser);
                    properties.put("DB_PASSWORD", dbPassword);
                    properties.put("SERVER_PORT", serverPort);
                    properties.put("INITIAL_PASSWORD", initialAdminPassword);

                    createFromTemplateAndOpen(project, TEMPLATE_LSFUSION_SETTINGS, settingsFile, properties);
                }

                //Create run configuration
                RunManager runManager = RunManager.getInstance(project);
                RunnerAndConfigurationSettings runConfiguration = runManager.createRunConfiguration("Run lsFusion server", LSFusionRunConfigurationType.getInstance().getConfigurationFactory());
                ((LSFusionRunConfiguration)runConfiguration.getConfiguration()).setModule(rootModel.getModule());
                ((LSFusionRunConfiguration)runConfiguration.getConfiguration()).setWorkingDirectory(contentEntryPath);
                if (runManager instanceof RunManagerEx) {
                    runManager.addConfiguration(runConfiguration, false);
                    runManager.setSelectedConfiguration(runConfiguration);
                }
            }

            List<Pair<String, String>> sourcePaths = getSourcePaths();
            LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
            if (!sourcePaths.isEmpty()) {
                Properties properties = new Properties();
                properties.put("NAME", MAIN_LSF_NAME);
                String srcPath = sourcePaths.iterator().next().first;
                String mainPath = srcPath + "/main";
                
                String javaPath = mainPath + "/java";
                new File(javaPath).mkdirs();
                
                String lsfusionPath = mainPath + "/lsfusion";
                File lsfusionDir = new File(lsfusionPath);
                lsfusionDir.mkdirs();

                String resourcesPath = mainPath + "/resources";
                new File(resourcesPath).mkdirs();

                String testPath = srcPath + "/test";
                new File(testPath).mkdirs();

                ContentEntry contentEntry = doAddContentEntry(rootModel);
                if (contentEntry != null) {
                    contentEntry.clearSourceFolders();
                    
                    VirtualFile lsfusionVirtualFile = localFileSystem.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(lsfusionPath));
                    if (lsfusionVirtualFile != null) {
                        contentEntry.addSourceFolder(lsfusionVirtualFile, false);
                    }
                    VirtualFile javaVirtualFile = localFileSystem.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(javaPath));
                    if (javaVirtualFile != null) {
                        contentEntry.addSourceFolder(javaVirtualFile, false);
                    }
                    VirtualFile testVirtualFile = localFileSystem.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(testPath));
                    if (testVirtualFile != null) {
                        contentEntry.addSourceFolder(testVirtualFile, true);
                    }
                    VirtualFile resourcesVirtualFile = localFileSystem.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(resourcesPath));
                    if (resourcesVirtualFile != null) {
                        contentEntry.addSourceFolder(resourcesVirtualFile, JavaResourceRootType.RESOURCE);
                    }
                }
                createFromTemplateAndOpen(project, TEMPLATE_LSFUSION_MODULE, new File(lsfusionDir, MAIN_LSF_FILE), properties);
            }

            localFileSystem.refresh(true);
        }
    }

    @Override
    public ModuleType<?> getModuleType() {
        return LSFModuleType.INSTANCE;
    }

    // c/p from JavaModuleBuilder
    public List<Pair<String,String>> getSourcePaths() {
        final List<Pair<String, String>> paths = new ArrayList<>();
        @NonNls final String path = getContentEntryPath() + File.separator + "src";
        new File(path).mkdirs();
        paths.add(Pair.create(path, ""));
        return paths;
    }
    
    private void createFromTemplateAndOpen(final Project project, String templateName, final File templateFile, Properties properties) {
        try {
            String content = FileTemplateManager.getInstance(project).getInternalTemplate(templateName).getText(properties);


            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(templateFile), StandardCharsets.UTF_8));
            writer.print(content);
            writer.close();

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(templateFile);
                    if (file != null) {
                        FileEditorManager.getInstance(project).openFile(file, true);
                    }
                }
            });

        } catch (IOException e) {
            Messages.showErrorDialog("Can't create settings.properties file: " + e.getMessage(), CommonBundle.getErrorTitle());
        }
    }
}
