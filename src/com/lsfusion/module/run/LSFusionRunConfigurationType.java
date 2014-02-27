package com.lsfusion.module.run;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.LSFBundle;
import com.lsfusion.LSFIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LSFusionRunConfigurationType implements ConfigurationType {
    private final ConfigurationFactory myFactory;

    public LSFusionRunConfigurationType() {
        myFactory = new ConfigurationFactoryEx(this) {
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new LSFusionRunConfiguration("", project, this);
            }

            @Override
            public void onNewConfigurationCreated(@NotNull RunConfiguration configuration) {
                ((LSFusionRunConfiguration)configuration).onNewConfigurationCreated();
            }
        };
    }

    public String getDisplayName() {
        return LSFBundle.message("run.configuration.display.name");
    }

    public String getConfigurationTypeDescription() {
        return LSFBundle.message("run.configuration.description");
    }

    public Icon getIcon() {
        return LSFIcons.RUN;
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myFactory};
    }

    public ConfigurationFactory getConfigurationFactory() {
        return myFactory;
    }

    @NotNull
    public String getId() {
        return "lsfusion";
    }

    @NotNull
    public static LSFusionRunConfigurationType getInstance() {
        LSFusionRunConfigurationType instance = ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), LSFusionRunConfigurationType.class);
        if (instance == null) {
            throw new NullPointerException("Can't find instance of LSFusionRunConfigurationType");
        }
        return instance;
    }
}
