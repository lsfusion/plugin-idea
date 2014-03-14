package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.FlexAlignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

public class GridView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("tabVertical").setExpert(),
            new ReflectionProperty("autoHide").setExpert(),
            new ReflectionProperty("quickSearch").setExpert()
    );

    public boolean tabVertical = false;
    public boolean autoHide = false;
    private boolean quickSearch = false;

    public GridView() {
        this("");
    }

    public GridView(String sID) {
        super(sID);
        flex = 1;
        alignment = FlexAlignment.STRETCH;
    }

    @Override
    public String getCaption() {
        return "Grid";
    }

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }
    
    public void setTabVertical(boolean tabVertical) {
        this.tabVertical = tabVertical;
    }

    public void setAutoHide(boolean autoHide) {
        this.autoHide = autoHide;
    }

    public void setQuickSearch(boolean quickSearch) {
        this.quickSearch = quickSearch;
    }
    
    public boolean isTabVertical() {
        return tabVertical;
    }

    public boolean isAutoHide() {
        return autoHide;
    }

    public boolean isQuickSearch() {
        return quickSearch;
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.GRID;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection) {
        JBTable table = new JBTable(new DefaultTableModel(15, 7));
        return new JBScrollPane(table);
    }
}
