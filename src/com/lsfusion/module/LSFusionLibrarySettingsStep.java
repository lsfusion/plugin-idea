package com.lsfusion.module;

import com.intellij.ide.projectWizard.ProjectSettingsStep;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;

import javax.swing.*;

public class LSFusionLibrarySettingsStep extends ModuleWizardStep {
    private final static String DB_HOST_KEY = "lsfusion.dbhost.key";
    private final static String DB_PORT_KEY = "lsfusion.dbport.key";
    private final static String DB_USER_KEY = "lsfusion.dbuser.key";
    private final static String DB_PASS_KEY = "lsfusion.dbpass.key";

    private final LSFusionModuleBuilder moduleBuilder;

    private final SettingsPanel settingsPanel;

    public LSFusionLibrarySettingsStep(LSFusionModuleBuilder moduleBuilder, SettingsStep settingsStep) {
        assert settingsStep != null;

        this.moduleBuilder = moduleBuilder;

        //noinspection RedundantCast
        JTextField moduleNameField = settingsStep instanceof ProjectSettingsStep ? ((ProjectSettingsStep) settingsStep).getModuleNameField() : null;
        settingsPanel = createSettingsPanel(moduleNameField);

        settingsStep.addSettingsComponent(settingsPanel.getPanel());
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
    public void updateDataModel() {

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
}
