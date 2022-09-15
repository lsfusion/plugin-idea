package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.ui.FlexPanel;
import com.lsfusion.design.ui.JComponentPanel;
import com.lsfusion.design.ui.ToolbarGridButton;

import javax.swing.*;
import java.util.HashSet;
import java.util.Map;

public class FilterControlsView extends ComponentView {
    public FilterControlsView() {
        super("");
    }
    
    @Override
    public String getCaption() {
        return "Filter Controls";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.TOOLBAR;
    }

    @Override
    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget, HashSet<ComponentView> recursionGuard) {
        FlexPanel panel = new FlexPanel(false);
//        JComponentPanel panel = new JComponentPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
//        panel.setAlignmentY(Component.TOP_ALIGNMENT);

        panel.add(new ToolbarGridButton(LSFIcons.Design.FILTER_ADD));
        panel.add(new ToolbarGridButton(LSFIcons.Design.FILTER_APPLY));
        panel.add(new ToolbarGridButton(LSFIcons.Design.FILTER_RESET));
        
        return panel;
    }
}
