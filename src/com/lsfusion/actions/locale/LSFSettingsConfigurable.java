package com.lsfusion.actions.locale;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.NlsContexts;
import com.lsfusion.documentation.LSFDocumentationProvider;
import com.lsfusion.lang.LSFResourceBundleUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Set;

public class LSFSettingsConfigurable implements Configurable {
    private JTextField field;
    private ComboBox<String> documentationVersionComboBox;
    private ComboBox<String> documentationLanguageComboBox;
    private String currentKey = null;
    private String currentDocumentationVersion = null;
    private String currentDocumentationLanguage = null;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "LSF settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        field = new JTextField(LSFResourceBundleUtils.getGoogleTranslateApiKey());
        JPanel settingsPanel = addElement(field, new JLabel("Google Translate api key"));
        settingsPanel.setLayout(new GridLayout());
        mainPanel.add(settingsPanel, BorderLayout.NORTH);

        documentationVersionComboBox = getComboBox(LSFDocumentationProvider.documentationVersionMap, LSFDocumentationProvider.getDocumentationVersion());
        JPanel documentationVersionSettingsPanel = addElement(documentationVersionComboBox, new JLabel("Documentation version"));
        mainPanel.add(documentationVersionSettingsPanel, BorderLayout.WEST);

        documentationLanguageComboBox = getComboBox(LSFDocumentationProvider.documentationLanguageMap, LSFDocumentationProvider.getDocumentationLanguage());
        JPanel documentationLanguageSettingsPanel = addElement(documentationLanguageComboBox, new JLabel("Documentation language"));
        mainPanel.add(documentationLanguageSettingsPanel);

        return mainPanel;
    }

    private ComboBox<String> getComboBox(Map<String, String> map, String defaultItem) {
        Set<String> keySet = map.keySet();
        ComboBox<String> comboBox = new ComboBox<>(keySet.toArray(new String[0]));
        comboBox.setItem(defaultItem);
        return comboBox;
    }

    private JPanel addElement(JComponent field, JLabel label) {
        JPanel panel = new JPanel();
        label.setLabelFor(field);

        panel.add(label, getGridBagConstraints(0));
        panel.add(field, getGridBagConstraints(1));

        return panel;
    }

    private GridBagConstraints getGridBagConstraints(int column) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = 0;
        gbc.fill = column == 0 ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        gbc.weightx = column == 0 ? 0.1 : 1.0;
        gbc.weighty = 1.0;
        return gbc;
    }

    @Override
    public boolean isModified() {
        return !field.getText().equals(currentKey)
                || !documentationVersionComboBox.getItem().equals(currentDocumentationVersion)
                || !documentationLanguageComboBox.getItem().equals(currentDocumentationLanguage);
    }

    @Override
    public void apply() throws ConfigurationException {
        currentKey = field.getText();
        LSFResourceBundleUtils.setGoogleTranslateApiKey(currentKey);

        currentDocumentationVersion = documentationVersionComboBox.getItem();
        LSFDocumentationProvider.setDocumentationVersion(currentDocumentationVersion);

        currentDocumentationLanguage = documentationLanguageComboBox.getItem();
        LSFDocumentationProvider.setDocumentationLanguage(currentDocumentationLanguage);
    }
}
