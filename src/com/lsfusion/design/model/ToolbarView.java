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

public class ToolbarView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("visible"),
            new ReflectionProperty("showGroupChange"),
            new ReflectionProperty("showCountRows"),
            new ReflectionProperty("showCalculateSum"),
            new ReflectionProperty("showGroupReport"),
            new ReflectionProperty("showPrint"),
            new ReflectionProperty("showXls"),
            new ReflectionProperty("showSettings")
    );

    public boolean visible = false;

    public boolean showGroupChange = true;
    public boolean showCountRows = true;
    public boolean showCalculateSum = true;
    public boolean showGroupReport = true;
    public boolean showPrint = true;
    public boolean showXls = true;
    public boolean showSettings = true;

    public boolean isTreeToolbar = false;

    public ToolbarView(boolean isTreeToolbar) {
        this("");
        this.isTreeToolbar = isTreeToolbar;
    }

    public ToolbarView(String sID) {
        super(sID);
    }

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getCaption() {
        return "Toolbar";
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setShowGroupChange(boolean showGroupChange) {
        this.showGroupChange = showGroupChange;
    }

    public void setShowCountQuantity(boolean showCountQuantity) {
        this.showCountRows = showCountQuantity;
    }

    public void setShowCalculateSum(boolean showCalculateSum) {
        this.showCalculateSum = showCalculateSum;
    }

    public void setShowGroup(boolean showGroup) {
        this.showGroupReport = showGroup;
    }

    public void setShowPrintGroup(boolean showPrintGroupButton) {
        this.showPrint = showPrintGroupButton;
    }

    public void setShowPrintGroupXls(boolean showPrintGroupXls) {
        this.showXls = showPrintGroupXls;
    }

    public void setShowSettings(boolean showSettings) {
        this.showSettings = showSettings;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isShowGroupChange() {
        return showGroupChange;
    }

    public boolean isShowCountRows() {
        return showCountRows;
    }

    public boolean isShowCalculateSum() {
        return showCalculateSum;
    }

    public boolean isShowGroupReport() {
        return showGroupReport;
    }

    public boolean isShowPrint() {
        return showPrint;
    }

    public boolean isShowXls() {
        return showXls;
    }

    public boolean isShowSettings() {
        return showSettings;
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

        panel.add(new ToolbarGridButton(LSFIcons.Design.FILTER_ADD));
        if (!isTreeToolbar) {
            if (isShowGroupChange() || isShowCountRows() || isShowCalculateSum() || isShowGroupChange()) {
                panel.add(Box.createHorizontalStrut(5));
            }
            if (isShowGroupChange()) {
                panel.add(new ToolbarGridButton(LSFIcons.Design.GROUP_CHANGE));
            }
            if (isShowCountRows()) {
                panel.add(new ToolbarGridButton(LSFIcons.Design.QUANTITY));
            }
            if (isShowCalculateSum()) {
                panel.add(new ToolbarGridButton(LSFIcons.Design.SUM));
            }
            if (isShowGroupChange()) {
                panel.add(new ToolbarGridButton(LSFIcons.Design.GROUP));
            }
            if (isShowPrint() || isShowXls()) {
                panel.add(Box.createHorizontalStrut(5));
            }
            if (isShowPrint()) {
                panel.add(new ToolbarGridButton(LSFIcons.Design.PRINT_GROUP));
            }
            if (isShowXls()) {
                panel.add(new ToolbarGridButton(LSFIcons.Design.PRINT_XLS));
            }
            if (isShowSettings()) {
                panel.add(Box.createHorizontalStrut(5));
                panel.add(new ToolbarGridButton(LSFIcons.Design.PREFERENCES));
            }
        }
        return panel;
    }
}
