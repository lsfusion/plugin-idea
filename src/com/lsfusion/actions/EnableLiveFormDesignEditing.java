package com.lsfusion.actions;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.lsfusion.design.FormDesignChangeDetector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EnableLiveFormDesignEditing extends ToggleAction {
    public static final String LSF_PROPERTY_LIVE_FORM_DESIG_EDITING_ON = "lsfusion.property.live.form.design.editing.on";

    @Override
    public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
        boolean selected = isEnableFormDesignEditingEnabled(getEventProject(anActionEvent));
        setToolwindow(anActionEvent, selected);
        return selected;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent anActionEvent, boolean selected) {
        final Project project = getEventProject(anActionEvent);
        if (project != null)
            PropertiesComponent.getInstance(project).setValue(LSF_PROPERTY_LIVE_FORM_DESIG_EDITING_ON, Boolean.toString(selected));

        setToolwindow(anActionEvent, selected);
    }

    private TimerListener timerListener;
    private void setToolwindow(AnActionEvent anActionEvent, boolean selected) {
        Project eventProject = getEventProject(anActionEvent);
        if (eventProject != null) {
            if (!selected) {
                if (timerListener != null) {
                    ActionManager.getInstance().removeTimerListener(timerListener);
                    timerListener = null;
                }
            } else if (timerListener == null) {
                timerListener = new TimerListener() {
                    @Override
                    public ModalityState getModalityState() {
                        return ModalityState.defaultModalityState();
                    }

                    @Override
                    public void run() {
                        PsiElement element = ConfigurationContext.getFromContext(DataManager.getInstance()
                                .getDataContext(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner())).getPsiLocation();

                        if (element != null)
                            FormDesignChangeDetector.showLiveDesign(eventProject, element, element.getContainingFile());
                    }
                };

                ActionManager.getInstance().addTimerListener(500, timerListener);
            }
        }
    }

    public static boolean isEnableFormDesignEditingEnabled(Project project) {
        return project != null && PropertiesComponent.getInstance(project).getBoolean(LSF_PROPERTY_LIVE_FORM_DESIG_EDITING_ON, true);
    }
}
