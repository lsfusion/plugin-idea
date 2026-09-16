package com.lsfusion.module;

import com.intellij.ui.DocumentAdapter;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.lsfusion.LSFBundle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class SettingsPanel {
    private final JPanel mainPanel;

    private final JCheckBox cbCreateSettingsFile = new JCheckBox(LSFBundle.message("module.wizard.create.settings.file"), true);
    private final JTextField databaseHostTextField = new JTextField();
    private final JTextField databasePortTextField = new JTextField();
    private final JTextField databaseUsernameTextField = new JTextField();
    private final JTextField databasePasswordTextField = new JTextField();
    private final JTextField serverPortTextField = new JTextField("7652");
    private final JTextField databaseNameTextField = new JTextField("lsfusion_untitled");
    private final JTextField initialAdminPasswordTextField = new JTextField();

    private boolean dbNameChangedByUser = false;
    private boolean dbNameDocListenerEnabled = true;

    public SettingsPanel(final JTextField moduleNameField, String dbHost, String dbPort, String dbUser, String dbPass) {
        JPanel propertiesPanel = FormBuilder.createFormBuilder()
                .addComponent(new JLabel(LSFBundle.message("module.wizard.dbsettings")))
                .setFormLeftIndent(10)
                .addLabeledComponent(LSFBundle.message("module.wizard.dbhost"), databaseHostTextField)
                .addLabeledComponent(LSFBundle.message("module.wizard.dbport"), databasePortTextField)
                .addLabeledComponent(LSFBundle.message("module.wizard.dbname"), databaseNameTextField)
                .addLabeledComponent(LSFBundle.message("module.wizard.dbuser"), databaseUsernameTextField)
                .addLabeledComponent(LSFBundle.message("module.wizard.dbpass"), databasePasswordTextField)
                .setFormLeftIndent(0)
                .addComponent(new JLabel(LSFBundle.message("module.wizard.lsfusion.settings")))
                .setFormLeftIndent(10)
                .addLabeledComponent(LSFBundle.message("module.wizard.serverport"), serverPortTextField)
                .addLabeledComponent(LSFBundle.message("module.wizard.inital.adminpassword"), initialAdminPasswordTextField)
                .getPanel();
        mainPanel = FormBuilder.createFormBuilder()
                // FormBuilder stretches components across the row; wrapped, the checkbox reacts to clicks on itself only
                .addComponent(JBUI.Panels.simplePanel().addToLeft(cbCreateSettingsFile))
                .addComponent(propertiesPanel)
                .getPanel();

        setDbHost(dbHost);
        setDbPort(dbPort);
        setDbUser(dbUser);
        setDbPass(dbPass);

        cbCreateSettingsFile.addChangeListener(e -> propertiesPanel.setVisible(cbCreateSettingsFile.isSelected()));
        if (moduleNameField != null) {
            moduleNameField.getDocument().addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(DocumentEvent e) {
                    setDbNameFromModuleName(moduleNameField.getText());
                }
            });
        }
        databaseNameTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                if (dbNameDocListenerEnabled) {
                    dbNameChangedByUser = true;
                }
            }
        });
    }

    private void setDbNameFromModuleName(String moduleName) {
        if (!dbNameChangedByUser) {
            dbNameDocListenerEnabled = false;

            moduleName = "lsfusion_" + moduleName.replace(" ", "_").replaceAll("[^\\w\\d]", "");
            databaseNameTextField.setText(moduleName);

            dbNameDocListenerEnabled = true;
        }
    }

    public boolean isCreateSettingsFile() {
        return cbCreateSettingsFile.isSelected();
    }

    public String getDbHost() {
        return databaseHostTextField.getText();
    }

    public void setDbHost(String dbHost) {
        databaseHostTextField.setText(dbHost);
    }

    public String getDbPort() {
        return databasePortTextField.getText();
    }

    public void setDbPort(String dbPort) {
        databasePortTextField.setText(dbPort);
    }

    public String getDbName() {
        return databaseNameTextField.getText();
    }

    public void setDbName(String dbName) {
        databaseNameTextField.setText(dbName);
    }

    public String getDbUser() {
        return databaseUsernameTextField.getText();
    }

    public void setDbUser(String dbUser) {
        databaseUsernameTextField.setText(dbUser);
    }

    public String getDbPass() {
        return databasePasswordTextField.getText();
    }

    public void setDbPass(String dbPass) {
        databasePasswordTextField.setText(dbPass);
    }

    public String getServerPort() {
        return serverPortTextField.getText();
    }

    public String getInitialAdminPassword() {
        return initialAdminPasswordTextField.getText();
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}
