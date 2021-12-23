package com.lsfusion.module;

import com.intellij.facet.impl.ui.libraries.LibraryCompositionSettings;
import com.intellij.framework.library.FrameworkLibraryVersionFilter;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.util.io.FileUtil;
import com.lsfusion.LSFBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;

public class LSFusionLibraryProjectTypeStep extends ModuleWizardStep {
    private final LSFusionModuleBuilder moduleBuilder;

    private final ModuleWizardStep javaProjectTypeStep;

    private final LibrariesContainer librariesContainer;

    private final LibraryOptionsPanel libraryPanel;

    private LibraryCompositionSettings libraryCompositionSettings;

    public LSFusionLibraryProjectTypeStep(LSFusionModuleBuilder moduleBuilder, SettingsStep settingsStep) {
        assert settingsStep != null;

        this.moduleBuilder = moduleBuilder;
        this.librariesContainer = LibrariesContainerFactory.createContainer(settingsStep.getContext().getProject());

        moduleBuilder.addModuleConfigurationUpdater(new ModuleBuilder.ModuleConfigurationUpdater() {
            @Override
            public void update(@NotNull Module module, @NotNull ModifiableRootModel rootModel) {
                if (libraryCompositionSettings != null) {
                    libraryCompositionSettings.addLibraries(rootModel, new ArrayList<>(), librariesContainer);
                }
            }
        });

        javaProjectTypeStep = JavaModuleType.getModuleType().modifyProjectTypeStep(settingsStep, moduleBuilder);
        libraryPanel = createLibraryPanel();

        settingsStep.addSettingsField(LSFBundle.message("module.wizard.lsfusion.library"), libraryPanel.getSimplePanel());
    }

    private LibraryOptionsPanel createLibraryPanel() {
        final LSFusionLibraryDescription libraryDescription = new LSFusionLibraryDescription();
        final String basePath = moduleBuilder.getContentEntryPath();
        final String baseDirPath = basePath != null ? FileUtil.toSystemIndependentName(basePath) : "";
        return new LibraryOptionsPanel(libraryDescription, baseDirPath, FrameworkLibraryVersionFilter.ALL, librariesContainer);
    }

    @Override
    public boolean validate() throws ConfigurationException {
        return super.validate() && (javaProjectTypeStep == null || javaProjectTypeStep.validate());
    }

    @Override
    public void updateDataModel() {
        javaProjectTypeStep.updateDataModel();
        libraryCompositionSettings = libraryPanel.apply();
    }

    @Override
    public JComponent getComponent() {
        return null;
    }

    @Override
    public void disposeUIResources() {
        javaProjectTypeStep.disposeUIResources();
    }
}
