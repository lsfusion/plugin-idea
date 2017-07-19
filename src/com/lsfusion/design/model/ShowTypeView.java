package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.ui.JComponentPanel;
import com.lsfusion.design.ui.ToolbarGridButton;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ShowTypeView extends ComponentView {
    private GroupObjectView groupObject;

    public ShowTypeView(GroupObjectView groupObject) {
        this("");
        this.groupObject = groupObject;
    }

    public ShowTypeView(String sID) {
        super(sID);
    }

    @Override
    public String getCaption() {
        return "Show type selector";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.SHOW_TYPE;
    }

    @Override
    protected JComponentPanel createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget) {
        if (groupObject.entity.banClassView.size() > 1) {
            return null;
        }

        JComponentPanel panel = new JComponentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);

        panel.setPreferredSize(new Dimension(63, 20));

        panel.add(new ToolbarGridButton(LSFIcons.Design.VIEW_GRID));
        panel.add(new ToolbarGridButton(LSFIcons.Design.VIEW_PANEL));
        panel.add(new ToolbarGridButton(LSFIcons.Design.VIEW_HIDE));

        return panel;
    }
}
