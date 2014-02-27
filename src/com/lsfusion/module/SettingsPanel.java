package com.lsfusion.module;

import com.intellij.ui.DocumentAdapter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

public class SettingsPanel {
    private JPanel mainPanel;

    private JPanel propertiesPanel;

    private JCheckBox cbCreateSettingsFile;
    private JTextField databaseHostTextField;
    private JTextField databasePortTextField;
    private JTextField databaseUsernameTextField;
    private JTextField databasePasswordTextField;
    private JTextField serverPortTextField;
    private JTextField databaseNameTextField;
    private JTextField initialAdminPasswordTextField;

    private boolean dbNameChangedByUser = false;
    private boolean dbNameDocListenerEnabled = true;

    public SettingsPanel(final JTextField moduleNameField, String dbHost, String dbPort, String dbUser, String dbPass) {
        setDbHost(dbHost);
        setDbPort(dbPort);
        setDbUser(dbUser);
        setDbPass(dbPass);

        cbCreateSettingsFile.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                propertiesPanel.setVisible(cbCreateSettingsFile.isSelected());
            }
        });
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
