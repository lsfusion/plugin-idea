package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
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
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        FlexPanel topPanel = new FlexPanel(false);
        topPanel.add(new ToolbarGridButton(LSFIcons.Design.FILTER));
        topPanel.add(new ToolbarGridButton(LSFIcons.Design.FILTER_ADD));

        FlexPanel bottomPanel = new FlexPanel(false);
        bottomPanel.add(createComboBox(220, "someproperty(id)", "otherprop(id)", "..."), 0, FlexAlignment.CENTER);
        bottomPanel.add(new JBCheckBox("NOT"), 0, FlexAlignment.CENTER);
        bottomPanel.add(createComboBox(40, "=", "!=", "..."), 0, FlexAlignment.CENTER);
        bottomPanel.add(createComboBox(70, "Value", "Object", "..."), 0, FlexAlignment.CENTER);
        bottomPanel.add(Box.createHorizontalStrut(5), 0, FlexAlignment.CENTER);
        bottomPanel.add(new SingleCellTable(), 0, FlexAlignment.CENTER);
        bottomPanel.add(new ToolbarGridButton(LSFIcons.Design.FILTER_DEL), 0, FlexAlignment.CENTER);

        FlexPanel panel = new FlexPanel(true);
        panel.add(topPanel, 0, FlexAlignment.LEADING);
        panel.add(bottomPanel, 0, FlexAlignment.LEADING);

        return panel;
    }

    private ComboBox createComboBox(int width, String... variants) {
        return new ComboBox(variants, width);
    }
}
