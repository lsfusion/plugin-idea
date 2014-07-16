package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.design.ui.FlexAlignment;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class GridView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("tabVertical").setExpert(),
            new ReflectionProperty("autoHide").setExpert(),
            new ReflectionProperty("quickSearch").setExpert()
    );

    GridTableModel model = new GridTableModel();

    public boolean tabVertical = false;
    public boolean autoHide = false;
    public boolean quickSearch = false;

    private GroupObjectView groupObject;

    private JComponent component;

    public GridView(GroupObjectView groupObject) {
        this("");
        this.groupObject = groupObject;
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

    public boolean isVisible() {
        return component != null;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        if (groupObject.entity.initClassView == ClassViewType.GRID && model.getColumnCount() > 0) {
            GridTable gridTable = new GridTable(model);
            return component = new JBScrollPane(gridTable);
        } else {
            return null;
        }
    }

    public void addPropertyDraw(PropertyDrawView property) {
        model.addPropertyDraw(property);
    }
}
