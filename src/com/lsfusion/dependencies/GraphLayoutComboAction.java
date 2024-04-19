package com.lsfusion.dependencies;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.vcs.changes.committed.LabeledComboBoxAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class GraphLayoutComboAction extends LabeledComboBoxAction {
    public final static String HIERARCHICAL_LAYOUT = "Hierarchical";
    public final static String COMPACT_TREE_LAYOUT = "Compact Tree";
    public final static String TREE_LAYOUT = "Tree";
    public final static String SIMPLE_LAYOUT = "Simple";
    public final static String ORGANIC_LAYOUT = "Organic";
    public final static String FAST_ORGANIC_LAYOUT = "Fast Organic";
    public final static String SELF_ORGANIZING_ORGANIC_LAYOUT = "Self Organizing Organic";
    public final static String RADIAL_TREE_LAYOUT = "Radial Tree";

    private String currentLayout = COMPACT_TREE_LAYOUT;

    protected GraphLayoutComboAction(@NotNull String label) {
        super(label);
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent button) {
        final DefaultActionGroup group = new DefaultActionGroup();
        String[] layouts = new String[]{COMPACT_TREE_LAYOUT, TREE_LAYOUT, HIERARCHICAL_LAYOUT, SIMPLE_LAYOUT, 
                RADIAL_TREE_LAYOUT, ORGANIC_LAYOUT, FAST_ORGANIC_LAYOUT, SELF_ORGANIZING_ORGANIC_LAYOUT};
        for (final String layout : layouts) {
            group.add(new AnAction(layout) {
                @Override
                public void actionPerformed(final AnActionEvent e) {
                    if (!currentLayout.equals(layout)) {
                        currentLayout = layout;
                        changeLayout(true);
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
        presentation.setText(currentLayout);
    }
    
    public String getCurrentLayout() {
        return currentLayout;
    }

    protected abstract void changeLayout(boolean update);

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
