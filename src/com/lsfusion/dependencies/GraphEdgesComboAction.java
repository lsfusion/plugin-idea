package com.lsfusion.dependencies;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.vcs.changes.committed.LabeledComboBoxAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class GraphEdgesComboAction extends LabeledComboBoxAction {
    public final static String NORMAL = "Normal";
    public final static String ALL_EDGES = "All edges";
    public final static String ONLY_LEAFS = "Only leafs";

    private String currentEdges = NORMAL;

    protected GraphEdgesComboAction(@NotNull String label) {
        super(label);
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent button) {
        final DefaultActionGroup group = new DefaultActionGroup();
        for (final String layout : new String[]{NORMAL, ALL_EDGES, ONLY_LEAFS}) {
            group.add(new AnAction(layout) {
                @Override
                public void actionPerformed(final AnActionEvent e) {
                    if (!currentEdges.equals(layout)) {
                        currentEdges = layout;
                        changeEdges();

                    }
                }
            });
        }
        return group;
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        presentation.setText(currentEdges);
    }
    
    public String getCurrentEdges() {
        return currentEdges;
    }

    protected abstract void changeEdges();

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
