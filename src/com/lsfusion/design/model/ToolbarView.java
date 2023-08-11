package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.JComponentPanel;
import com.lsfusion.design.ui.ToolbarGridButton;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/*adding new property:
1. add to PROPERTIES
2. create field
3. create setter in Proxy*/

public class ToolbarView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("visible"),
            new ReflectionProperty("showCountQuantity"),
            new ReflectionProperty("showCalculateSum"),
            new ReflectionProperty("showGroup"),
            new ReflectionProperty("showPrintGroupXls"),
            new ReflectionProperty("showSettings")
    );

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    public boolean visible;

    public boolean showCountQuantity = true;
    public boolean showCalculateSum = true;
    public boolean showGroup = true;
    public boolean showPrintGroupXls = true;
    public boolean showSettings = true;

    public boolean isTreeToolbar = false;

    public ToolbarView(boolean isTreeToolbar) {
        this("");
        this.isTreeToolbar = isTreeToolbar;
    }

    public ToolbarView(String sID) {
        super(sID);
    }

    public boolean isShowCountQuantity() {
        return showCountQuantity;
    }

    public boolean isShowCalculateSum() {
        return showCalculateSum;
    }

    public boolean isShowGroup() {
        return showGroup;
    }

    public boolean isShowPrintGroupXls() {
        return showPrintGroupXls;
    }

    public boolean isShowSettings() {
        return showSettings;
    }

    @Override
    public String getCaption() {
        return "Toolbar";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.TOOLBAR;
    }

    @Override
    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget, HashSet<ComponentView> recursionGuard) {
        if (!visible) {
            return null;
        }
        JComponentPanel panel = new JComponentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);


        if (isTreeToolbar) {
            panel.add(new ToolbarGridButton(LSFIcons.Design.FILTER));
        } else {
            if (isShowGroup()) {
                panel.add(new ToolbarGridButton(LSFIcons.Design.PIVOT));
                addSeparator(panel);
            }
            panel.add(new ToolbarGridButton(LSFIcons.Design.FILTER));
            if (isShowSettings()) {
                panel.add(new ToolbarGridButton(LSFIcons.Design.PREFERENCES));
            }
            if (isShowCountQuantity() || isShowCalculateSum()) {
                addSeparator(panel);
                if (isShowCountQuantity()) {
                    panel.add(new ToolbarGridButton(LSFIcons.Design.QUANTITY));
                }
                if (isShowCalculateSum()) {
                    panel.add(new ToolbarGridButton(LSFIcons.Design.SUM));
                }
            }
            if (isShowPrintGroupXls()) {
                addSeparator(panel);
                panel.add(new ToolbarGridButton(LSFIcons.Design.PRINT_XLS));
            }
        }
        return panel;
    }

    public void addSeparator(JComponentPanel panel) {
        panel.add(Box.createHorizontalStrut(2));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(Box.createHorizontalStrut(2));
    }
}
