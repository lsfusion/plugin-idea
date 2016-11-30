package com.lsfusion.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.text.StringTokenizer;
import com.lsfusion.lang.meta.MetaChangeDetector;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

public class MetaCodeEnableAction extends AnAction {
    private final String INCLUDED_MODULES = "INCLUDED_MODULES_META_CODE";
    private Project project;

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = getEventProject(e);
        if (e.getInputEvent() instanceof KeyEvent) {
            boolean enabled = MetaChangeDetector.getInstance(project).getMetaEnabled();
            IncludeModulesDialog dialog = new IncludeModulesDialog(enabled);
            dialog.show();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        if (e.getProject() != null) {
            e.getPresentation().setText(MetaChangeDetector.getInstance(e.getProject()).getMetaEnabled() ? "Disable _meta" : "Enable _meta");
        }
    }

    private class IncludeModulesDialog extends DialogWrapper {
        boolean enabled;
        private JTextField modulesToInclude;

        IncludeModulesDialog(boolean enabled) {
            super(project);
            this.enabled = enabled;
            init();
            setTitle((enabled ? "Disable" : "Enable") + " Meta Action");
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return modulesToInclude;
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel container = new JPanel(new BorderLayout());

            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            JLabel confirmLabel = new JLabel(String.format("Are you sure you want to %s meta code?", enabled ? "disable" : "enable"));
            JLabel label = new JLabel(String.format("Modules to be included in %s metacode: ", enabled ? "disabling" : "enabling"));

            modulesToInclude = new JTextField(propertiesComponent.getValue(INCLUDED_MODULES));
            modulesToInclude.setColumns(30);
            panel.add(label);
            panel.add(modulesToInclude);
            container.add(panel);

            container.add(confirmLabel, BorderLayout.NORTH);
            return container;
        }

        @Override
        protected void doOKAction() {
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
            propertiesComponent.setValue(INCLUDED_MODULES, modulesToInclude.getText());

            StringTokenizer tokenizer = new StringTokenizer(modulesToInclude.getText(), ",;");
            final java.util.List<String> modules = new ArrayList<>();
            while (tokenizer.hasMoreElements()) {
                modules.add(tokenizer.nextToken().trim());
            }
            super.doOKAction();
            MetaChangeDetector.getInstance(project).toggleMetaEnabled(modules);
        }
    }
}
