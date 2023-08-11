package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.JComponentPanel;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/*adding new property:
1. add to PROPERTIES
2. create field
3. create setter in Proxy*/

public class GridView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("autoSize"),
            new ReflectionProperty("boxed"),
            new ReflectionProperty("tabVertical"),
            new ReflectionProperty("quickSearch"),
            new ReflectionProperty("headerHeight"),
            new ReflectionProperty("resizeOverflow"),
            new ReflectionProperty("lineWidth"),
            new ReflectionProperty("lineHeight")
    );

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    public boolean autoSize;

    public boolean boxed;

    public boolean tabVertical;
    public boolean quickSearch;

    public int headerHeight;

    public boolean resizeOverflow;

    public int lineWidth = 0;
    public int lineHeight = 0;

    public GridView(GroupObjectView groupObject) {
        this("");
        this.groupObject = groupObject;
    }

    public GridView(String sID) {
        super(sID);
    }

    GridTableModel model = new GridTableModel();

    private GroupObjectView groupObject;

    private JComponentPanel component;

    @Override
    public double getBaseDefaultFlex(FormEntity formEntity) {
        return 1;
    }

    @Override
    public FlexAlignment getBaseDefaultAlignment(ContainerView container) {
        return FlexAlignment.STRETCH;
    }

    @Override
    public String getCaption() {
        return "Grid";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.GRID;
    }

    public boolean isVisible() {
        return component != null;
    }

    @Override
    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget, HashSet<ComponentView> recursionGuard) {
        if (groupObject.entity.initClassView == ClassViewType.GRID && model.getColumnCount() > 0) {
            GridTable gridTable = new GridTable(autoSize, model, headerHeight);
            JBScrollPane scrollPane = new JBScrollPane(gridTable) {
                @Override
                public boolean isValidateRoot() {
                    return false;
                }
            };
            
            return this.component = new JComponentPanel(scrollPane);
        } else {
            return null;
        }
    }

    public void addPropertyDraw(PropertyDrawView property) {
        model.addPropertyDraw(property);
    }
}
