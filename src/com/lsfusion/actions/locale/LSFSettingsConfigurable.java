package com.lsfusion.actions.locale;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.lsfusion.lang.LSFResourceBundleUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class LSFSettingsConfigurable implements Configurable {
    JTextField field;
    String currentKey = null;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "LSF settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());
        mainPanel.add(settingsPanel, BorderLayout.NORTH);

        field = new JTextField(LSFResourceBundleUtils.getGoogleTranslateApiKey());

        JLabel googleTranslateLabel = new JLabel("Google Translate api key");
        googleTranslateLabel.setLabelFor(field);

        settingsPanel.add(googleTranslateLabel, getGridBagConstraints(0));
        settingsPanel.add(field, getGridBagConstraints(1));

        return mainPanel;
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
        return !field.getText().equals(currentKey);
    }

    @Override
    public void apply() throws ConfigurationException {
        currentKey = field.getText();
        LSFResourceBundleUtils.setGoogleTranslateApiKey(currentKey);
    }
}
