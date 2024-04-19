package com.lsfusion.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.util.LSFFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MetaCodeEnableAction extends AnAction {
    private static final String INCLUDED_MODULES = "INCLUDED_MODULES_META_CODE";
    private Project project;
    PropertiesComponent propertiesComponent;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project = getEventProject(e);
        if(project != null) {
            propertiesComponent = PropertiesComponent.getInstance(project);
            MetaCodeEnableDialog dialog = new MetaCodeEnableDialog(LSFFileUtils.getModules(project), MetaChangeDetector.getInstance(project).getMetaEnabled());
            dialog.show();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        if (e.getProject() != null) {
            e.getPresentation().setText(MetaChangeDetector.getInstance(e.getProject()).getMetaEnabled() ? "Disable _meta" : "Enable _meta");
        }
    }

    private class MetaCodeEnableDialog extends DialogWrapper {
        Module[] modules;
        boolean enabled;
        CheckBoxGroup modulesCheckBoxGroup;

        MetaCodeEnableDialog(Module[] modules, boolean enabled) {
            super(project);
            this.modules = modules;
            this.enabled = enabled;
            init();
            setTitle((enabled ? "Disable" : "Enable") + " Meta Action");
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel container = new JPanel(new BorderLayout());

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            JLabel confirmLabel = new JLabel(String.format("Are you sure you want to %s meta code?", enabled ? "disable" : "enable"));
            confirmLabel.setBorder(JBUI.Borders.emptyBottom(10));

            if(modules.length > 1) {
                modulesCheckBoxGroup = new CheckBoxGroup(modules, getIncludedModules());
                panel.add(modulesCheckBoxGroup);
            }
            container.add(panel);

            container.add(confirmLabel, BorderLayout.NORTH);
            return container;
        }

        @Override
        protected void doOKAction() {
            List<String> includedModules = modulesCheckBoxGroup != null ? modulesCheckBoxGroup.getIncludedModules() : new ArrayList<>();
            setIncludedModules(includedModules);

            super.doOKAction();
            MetaChangeDetector.getInstance(project).toggleMetaEnabled(includedModules.size() == modules.length ? new ArrayList<>() : includedModules);
        }

        private List<String> getIncludedModules() {
            String includedModules = propertiesComponent.getValue(INCLUDED_MODULES);
            return includedModules != null && !includedModules.isEmpty() ? Arrays.asList(includedModules.split(",")) : new ArrayList<>();
        }

        private void setIncludedModules(List<String> includedModules) {
            propertiesComponent.setValue(INCLUDED_MODULES, StringUtils.join(includedModules, ","));
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
