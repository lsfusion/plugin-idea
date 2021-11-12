package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FilterView extends ComponentView {
    public static final List<Property> PROPERTIES = Collections.emptyList();

    private PropertyDrawView property;

    public FilterView(PropertyDrawView property) {
        this.property = property;
        setAlignment(FlexAlignment.START);
    }

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getCaption() {
        return "Filter";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.FILTER;
    }

    @Override
    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget, HashSet<ComponentView> recursionGuard) {
        JComponentPanel panel = new JComponentPanel();
        panel.setLayout(new FlexLayout(panel, false, Alignment.START));
        
        JBLabel propertyLabel = new JBLabel(property.getEditCaption());
        propertyLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        propertyLabel.setToolTipText(property.getCaption());
        
        JBLabel compareLabel = new JBLabel("=");
        compareLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

        SingleCellTable table = new SingleCellTable();
        table.setPreferredSize(new Dimension(property.getValueWidth(table), property.getValueHeight(table)));
        
        panel.add(propertyLabel, new FlexConstraints(FlexAlignment.CENTER, 0));
        panel.add(compareLabel, new FlexConstraints(FlexAlignment.CENTER, 0));
        panel.add(table, new FlexConstraints(FlexAlignment.CENTER, 0));

        return panel;
    }
}
