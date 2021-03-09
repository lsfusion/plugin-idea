package com.lsfusion.lang;

import com.intellij.lang.properties.PropertiesImplUtil;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class CreatePropertyFixDialog extends DialogWrapper {
    protected final Project project;
    private final Map<String, String> fileLocaleMap;

    protected JPanel centerPanel = new JPanel();
    private final ComboBox<String> propertiesFilesField = new ComboBox<>();
    private final JTextField keyField = new JTextField();
    private final JTextField valueField = new JTextField();

    protected CreatePropertyFixDialog(@NotNull Project project, String currentLang, List<PropertiesFile> propertiesFiles, String defaultKey, String defaultValue) {
        super(false);
        setTitle("Create property");

        this.project = project;

        fileLocaleMap = new HashMap<>();
        Vector<String> paths = new Vector<>();
        for (PropertiesFile propertiesFile : propertiesFiles) {
            final VirtualFile virtualFile = propertiesFile.getVirtualFile();
            if (virtualFile != null) {
                String path = virtualFile.getPath();
                fileLocaleMap.put(path, propertiesFile.getLocale().getLanguage());
                paths.add(path);
            }
        }
        propertiesFilesField.setModel(new DefaultComboBoxModel<>(paths));

        keyField.setText(defaultKey);
        keyField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                somethingChanged();
            }
        });
        somethingChanged();

        propertiesFilesField.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedFilePath = (String) e.getItem();
                updateValueField(defaultValue, currentLang, fileLocaleMap.get(selectedFilePath));
            }
        });
        updateValueField(defaultValue, currentLang, fileLocaleMap.get(propertiesFilesField.getItem()));

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        centerPanel.add(new JLabel("Select resource bundle"));
        centerPanel.add(propertiesFilesField);

        centerPanel.add(new JLabel("Key"));
        centerPanel.add(keyField);

        centerPanel.add(new JLabel("Value"));
        centerPanel.add(valueField);

        init();
    }

    private void updateValueField(String value, String currentLang, String targetLang) {
        valueField.setText(LSFResourceBundleUtils.getDefaultBundleValue(value, currentLang, targetLang));
    }

    protected void somethingChanged() {
        setOKActionEnabled(!StringUtil.isEmptyOrSpaces(getKey()));
    }

    protected PropertiesFile getPropertiesFilesField() {
        String path = FileUtil.toSystemIndependentName(propertiesFilesField.getItem());
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(path);
        return virtualFile != null ? PropertiesImplUtil.getPropertiesFile(PsiManager.getInstance(project).findFile(virtualFile)) : null;
    }

    @Override
    protected JComponent createCenterPanel() {
        return centerPanel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return valueField;
    }

    @Override
    protected void doOKAction() {
        String key = getKey();
        PropertiesFile propertiesFile = getPropertiesFilesField();
        if (propertiesFile.findPropertyByKey(key) != null) {
            Messages.showInfoMessage(project, String.format("Property '%s' already exists in the file '%s'", key, propertiesFile.getName()), "Property Already Exists");
        } else {
            super.doOKAction();
        }
    }

    public String getValue() {
        return valueField.getText();
    }

    public String getKey() {
        return keyField.getText();
    }
}
