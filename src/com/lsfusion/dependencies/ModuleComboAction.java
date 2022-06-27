package com.lsfusion.dependencies;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.vcs.changes.committed.LabeledComboBoxAction;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.Module;

import javax.swing.*;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleComboAction extends LabeledComboBoxAction {
    private final static String ALL = "ALL";
    private final Map<String, Module> modulesMap;
    private Module currentModule = null;

    protected ModuleComboAction(@NotNull String label, Map<String, Module> modulesMap) {
        super(label);
        this.modulesMap = modulesMap;
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent button) {
        final DefaultActionGroup group = new DefaultActionGroup();

        group.add(new AnAction(ALL) {
            @Override
            public void actionPerformed(@NotNull final AnActionEvent e) {
                currentModule = null;
            }
        });

        for (final String moduleName : modulesMap.keySet().stream().sorted().collect(Collectors.toList())) {
            group.add(new AnAction(moduleName) {
                @Override
                public void actionPerformed(@NotNull final AnActionEvent e) {
                    Module module = modulesMap.get(moduleName);
                    if (currentModule == null || !currentModule.equals(module)) {
                        currentModule = module;
                    }
                }
            });
        }
        return group;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        presentation.setText(currentModule != null ? currentModule.getName() : ALL);
    }
    
    public GlobalSearchScope getCurrentModuleScope() {
        return currentModule != null ? currentModule.getModuleWithDependenciesScope() : null;
    }
}
