package com.lsfusion.module.run;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.execution.ui.DefaultJreSelector;
import com.intellij.execution.ui.JrePathEditor;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LSFusionRunConfigurationEditor extends SettingsEditor<LSFusionRunConfiguration> {
    private JPanel myWholePanel;
    private CommonJavaParametersPanel myCommonProgramParameters;
    private LabeledComponent<ModulesComboBox> myModule;
    private JrePathEditor myJREPanel;
    private JCheckBox lightStartCheckBox;

    private final ConfigurationModuleSelector myModuleSelector;

    public LSFusionRunConfigurationEditor(Project project) {
        myModuleSelector = new ConfigurationModuleSelector(project, myModule.getComponent());
        myCommonProgramParameters.setModuleContext(myModuleSelector.getModule());
        myModule.getComponent().addActionListener(e -> myCommonProgramParameters.setModuleContext(myModuleSelector.getModule()));

        DefaultJreSelector defaultJreSelector = DefaultJreSelector.fromModuleDependencies(myModule.getComponent(), false);
        myJREPanel.setDefaultJreSelector(defaultJreSelector);
    }

    @Override
    protected void applyEditorTo(LSFusionRunConfiguration configuration) {
        myCommonProgramParameters.applyTo(configuration);
        myModuleSelector.applyTo(configuration);
        configuration.ALTERNATIVE_JRE_PATH = myJREPanel.getJrePathOrName();
        configuration.ALTERNATIVE_JRE_PATH_ENABLED = myJREPanel.isAlternativeJreSelected();
        configuration.LIGHT_START = lightStartCheckBox.isSelected();
    }

    public void resetEditorFrom(final LSFusionRunConfiguration configuration) {
        myCommonProgramParameters.reset(configuration);
        myModuleSelector.reset(configuration);
        myJREPanel.setPathOrName(configuration.ALTERNATIVE_JRE_PATH, configuration.ALTERNATIVE_JRE_PATH_ENABLED);
        lightStartCheckBox.setSelected(configuration.LIGHT_START);
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
