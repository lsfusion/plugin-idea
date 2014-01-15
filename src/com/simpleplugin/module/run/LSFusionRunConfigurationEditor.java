package com.simpleplugin.module.run;

import com.intellij.execution.ui.AlternativeJREPanel;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.execution.util.JreVersionDetector;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LSFusionRunConfigurationEditor extends SettingsEditor<LSFusionRunConfiguration> {
    private JPanel myWholePanel;
    private CommonJavaParametersPanel myCommonProgramParameters;
    private LabeledComponent<JComboBox> myModule;
    private AlternativeJREPanel myAlternativeJREPanel;

    private final Project myProject;
    private final ConfigurationModuleSelector myModuleSelector;
    private final JreVersionDetector myVersionDetector;

    public LSFusionRunConfigurationEditor(Project project) {
        myProject = project;
        myModuleSelector = new ConfigurationModuleSelector(project, myModule.getComponent());
        myCommonProgramParameters.setModuleContext(myModuleSelector.getModule());
        myModule.getComponent().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myCommonProgramParameters.setModuleContext(myModuleSelector.getModule());
            }
        });
        myVersionDetector = new JreVersionDetector();
    }

    @Override
    protected void applyEditorTo(LSFusionRunConfiguration configuration) throws ConfigurationException {
        myCommonProgramParameters.applyTo(configuration);
        myModuleSelector.applyTo(configuration);
        configuration.ALTERNATIVE_JRE_PATH = myAlternativeJREPanel.getPath();
        configuration.ALTERNATIVE_JRE_PATH_ENABLED = myAlternativeJREPanel.isPathEnabled();
    }

    public void resetEditorFrom(final LSFusionRunConfiguration configuration) {
        myCommonProgramParameters.reset(configuration);
        myModuleSelector.reset(configuration);
        myAlternativeJREPanel.init(configuration.ALTERNATIVE_JRE_PATH, configuration.ALTERNATIVE_JRE_PATH_ENABLED);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myWholePanel;
    }

    @Override
    protected void disposeEditor() {
    }
}
