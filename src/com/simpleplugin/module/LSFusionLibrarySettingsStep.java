package com.simpleplugin.module;

import com.intellij.facet.impl.ui.libraries.LibraryCompositionSettings;
import com.intellij.framework.library.FrameworkLibraryVersionFilter;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.newProjectWizard.SelectTemplateStep;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.util.io.FileUtil;
import com.simpleplugin.LSFBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;

public class LSFusionLibrarySettingsStep extends ModuleWizardStep {
    private final static String DB_HOST_KEY = "lsfusion.dbhost.key";
    private final static String DB_PORT_KEY = "lsfusion.dbport.key";
    private final static String DB_USER_KEY = "lsfusion.dbuser.key";
    private final static String DB_PASS_KEY = "lsfusion.dbpass.key";

    private final LSFusionModuleBuilder moduleBuilder;

    private final ModuleWizardStep javaSettingsStep;

    private final LibrariesContainer librariesContainer;

    private final LibraryOptionsPanel libraryPanel;

    private final SettingsPanel settingsPanel;

    private LibraryCompositionSettings libraryCompositionSettings;

    public LSFusionLibrarySettingsStep(LSFusionModuleBuilder moduleBuilder, SettingsStep settingsStep) {
        assert settingsStep != null;

        this.moduleBuilder = moduleBuilder;
        this.librariesContainer = LibrariesContainerFactory.createContainer(settingsStep.getContext().getProject());

        moduleBuilder.addModuleConfigurationUpdater(new ModuleBuilder.ModuleConfigurationUpdater() {
            @Override
            public void update(@NotNull Module module, @NotNull ModifiableRootModel rootModel) {
                if (libraryCompositionSettings != null) {
                    libraryCompositionSettings.addLibraries(rootModel, new ArrayList<Library>(), librariesContainer);
                }
            }
        });

        javaSettingsStep = JavaModuleType.getModuleType().modifySettingsStep(settingsStep, moduleBuilder);
        libraryPanel = createLibraryPanel();

        JTextField moduleNameField = settingsStep instanceof SelectTemplateStep ? ((SelectTemplateStep) settingsStep).getModuleNameField() : null;
        settingsPanel = createSettingsPanel(moduleNameField);

        settingsStep.addSettingsField(LSFBundle.message("module.wizard.lsfusion.library"), libraryPanel.getSimplePanel());
        settingsStep.addSettingsComponent(settingsPanel.getPanel());
    }

    private LibraryOptionsPanel createLibraryPanel() {
        final LSFusionLibraryDescription libraryDescription = new LSFusionLibraryDescription();
        final String basePath = moduleBuilder.getContentEntryPath();
        final String baseDirPath = basePath != null ? FileUtil.toSystemIndependentName(basePath) : "";
        return new LibraryOptionsPanel(libraryDescription, baseDirPath, FrameworkLibraryVersionFilter.ALL, librariesContainer);
    }

    private SettingsPanel createSettingsPanel(JTextField moduleNameField) {
        PropertiesComponent config = PropertiesComponent.getInstance();
        String dbHost = config.getValue(DB_HOST_KEY, "localhost");
        String dbPort = config.getValue(DB_PORT_KEY, "");
        String dbUser = config.getValue(DB_USER_KEY, "postgres");
        String dbPass = config.getValue(DB_PASS_KEY, "psw");

        return new SettingsPanel(moduleNameField, dbHost, dbPort, dbUser, dbPass);
    }

    @Override
    public boolean validate() throws ConfigurationException {
        return super.validate() && (javaSettingsStep == null || javaSettingsStep.validate());
    }

    @Override
    public void updateDataModel() {
        javaSettingsStep.updateDataModel();

        libraryCompositionSettings = libraryPanel.apply();

        moduleBuilder.setCreateSettingsFile(settingsPanel.isCreateSettingsFile());
        moduleBuilder.setDbHost(settingsPanel.getDbHost());
        moduleBuilder.setDbPort(settingsPanel.getDbPort());
        moduleBuilder.setDbName(settingsPanel.getDbName());
        moduleBuilder.setDbUser(settingsPanel.getDbUser());
        moduleBuilder.setDbPassword(settingsPanel.getDbPass());
        moduleBuilder.setServerPort(settingsPanel.getServerPort());
        moduleBuilder.setInitialAdminPassword(settingsPanel.getInitialAdminPassword());

        PropertiesComponent config = PropertiesComponent.getInstance();
        config.setValue(DB_HOST_KEY, settingsPanel.getDbHost());
        config.setValue(DB_PORT_KEY, settingsPanel.getDbPort());
        config.setValue(DB_USER_KEY, settingsPanel.getDbUser());
        config.setValue(DB_PASS_KEY, settingsPanel.getDbPass());
    }

    @Override
    public JComponent getComponent() {
        return null;
    }

    @Override
    public void disposeUIResources() {
        javaSettingsStep.disposeUIResources();
    }
}
