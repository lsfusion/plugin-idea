package com.lsfusion.module.run;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.execution.ui.DefaultJreSelector;
import com.intellij.execution.ui.JrePathEditor;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.lsfusion.LSFBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.BorderLayout;

public class LSFusionRunConfigurationEditor extends SettingsEditor<LSFusionRunConfiguration> {
    private final CommonJavaParametersPanel myCommonProgramParameters = new CommonJavaParametersPanel();
    private final LabeledComponent<ModulesComboBox> myModule =
            LabeledComponent.create(new ModulesComboBox(), LSFBundle.message("run.configuration.module.label"), BorderLayout.WEST);
    private final JrePathEditor myJREPanel = new JrePathEditor();
    private final JCheckBox lightStartCheckBox = new JCheckBox("Light start");
    private final JCheckBox devModeCheckBox = new JCheckBox("Dev mode");
    private final JPanel myWholePanel = FormBuilder.createFormBuilder()
            .addComponent(myCommonProgramParameters)
            .addVerticalGap(10)
            .addComponent(myModule)
            .addComponent(myJREPanel)
            // FormBuilder stretches components across the row; wrapped, a checkbox reacts to clicks on itself only
            .addComponent(JBUI.Panels.simplePanel().addToLeft(lightStartCheckBox))
            .addComponent(JBUI.Panels.simplePanel().addToLeft(devModeCheckBox))
            .addComponentFillVertically(JBUI.Panels.simplePanel(), 0)
            .getPanel();

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
        configuration.DEV_MODE = devModeCheckBox.isSelected();
    }

    public void resetEditorFrom(final LSFusionRunConfiguration configuration) {
        myCommonProgramParameters.reset(configuration);
        myModuleSelector.reset(configuration);
        myJREPanel.setPathOrName(configuration.ALTERNATIVE_JRE_PATH, configuration.ALTERNATIVE_JRE_PATH_ENABLED);
        lightStartCheckBox.setSelected(configuration.LIGHT_START);
        devModeCheckBox.setSelected(configuration.DEV_MODE);
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
