package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPanel;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.*;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class FilterView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("visible")
    );

    public boolean visible = true;

    public FilterView() {
        this("");
    }

    public FilterView(String sID) {
        super(sID);
        alignment = FlexAlignment.STRETCH;
    }

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getCaption() {
        return "Filter";
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.FILTER;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection) {
        JBPanel topPanel = new JBPanel();
        topPanel.setLayout(new FlexLayout(topPanel, false));
        topPanel.add(new ToolbarGridButton(LSFIcons.Design.FILTER), new FlexConstraints());
        topPanel.add(new ToolbarGridButton(LSFIcons.Design.FILTER_ADD), new FlexConstraints());

        JBPanel bottomPanel = new JBPanel();
        bottomPanel.setLayout(new FlexLayout(bottomPanel, false));
        bottomPanel.add(createComboBox(220, "someproperty(id)", "otherprop(id)", "..."), new FlexConstraints(FlexAlignment.CENTER, 0));
        bottomPanel.add(new JBCheckBox("NOT"), new FlexConstraints(FlexAlignment.CENTER, 0));
        bottomPanel.add(createComboBox(40, "=", "!=", "..."), new FlexConstraints(FlexAlignment.CENTER, 0));
        bottomPanel.add(createComboBox(70, "Value", "Object", "..."), new FlexConstraints(FlexAlignment.CENTER, 0));
        bottomPanel.add(Box.createHorizontalStrut(5), new FlexConstraints(FlexAlignment.CENTER, 0));
        bottomPanel.add(new SingleCellTable(), new FlexConstraints(FlexAlignment.CENTER, 0));
        bottomPanel.add(new ToolbarGridButton(LSFIcons.Design.FILTER_DEL), new FlexConstraints(FlexAlignment.CENTER, 0));

        JBPanel panel = new JBPanel();
        panel.setLayout(new FlexLayout(panel, true));
        panel.add(topPanel, new FlexConstraints(FlexAlignment.LEADING, 0));
        panel.add(bottomPanel, new FlexConstraints(FlexAlignment.LEADING, 0));

        return panel;
    }

    private ComboBox createComboBox(int width, String... variants) {
        return new ComboBox(variants, width);
    }
}
