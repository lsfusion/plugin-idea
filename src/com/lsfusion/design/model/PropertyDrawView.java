package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.CachableLayout;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.SingleCellTable;
import com.lsfusion.util.BaseUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.lsfusion.util.BaseUtils.*;
import static java.lang.Math.max;

public class PropertyDrawView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("showTableFirst").setExpert(),
            new ReflectionProperty("editOnSingleClick").setExpert(),
            new ReflectionProperty("hide").setExpert(),
            new ReflectionProperty("regexp"),
            new ReflectionProperty("regexpMessage"),
            new ReflectionProperty("maxValue"),
            new ReflectionProperty("echoSymbols"),
            new ReflectionProperty("contentType").setExpert(),
            new ReflectionProperty("minimumCharWidth").setExpert(),
            new ReflectionProperty("maximumCharWidth").setExpert(),
            new ReflectionProperty("preferredCharWidth"),
            new ReflectionProperty("editKey"),
            new ReflectionProperty("showEditKey").setExpert(),
            new ReflectionProperty("focusable"),
            new ReflectionProperty("panelLabelAbove"),
            new ReflectionProperty("caption"),
            new ReflectionProperty("clearText").setExpert(),
            new ReflectionProperty("toolTip"),
            new ReflectionProperty("askConfirm"),
            new ReflectionProperty("askConfirmMessage")
    );

    public boolean showTableFirst;
    public boolean editOnSingleClick;
    public boolean hide;
    public String regexp;
    public String regexpMessage;
    public Long maxValue;
    public boolean echoSymbols;
    public String contentType;

    public int minimumCharWidth;
    public int maximumCharWidth;
    public int preferredCharWidth;

    public KeyStroke editKey;
    public boolean showEditKey = true;

    public Boolean focusable;

    public boolean panelLabelAbove = false;

    public String caption;
    public boolean clearText;
    public String toolTip;

    public boolean askConfirm;
    public String askConfirmMessage;

    public boolean isAction;
    public boolean isForcedPanel;

    public PropertyDrawView() {
        this("");
    }

    public PropertyDrawView(String sID) {
        super(sID);
        setMargin(2);
    }

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    public String getEditCaption() {
        String caption = this.caption == null ? "" : this.caption; 
        return showEditKey && editKey != null
               ? caption + " (" + getKeyStrokeCaption(editKey) + ")"
               : caption;
    }

    public void setShowTableFirst(boolean showTableFirst) {
        this.showTableFirst = showTableFirst;
    }

    public void setEditOnSingleClick(boolean editOnSingleClick) {
        this.editOnSingleClick = editOnSingleClick;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public void setRegexpMessage(String regexpMessage) {
        this.regexpMessage = regexpMessage;
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    public void setEchoSymbols(boolean echoSymbols) {
        this.echoSymbols = echoSymbols;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setMinimumCharWidth(int minimumCharWidth) {
        this.minimumCharWidth = minimumCharWidth;
    }

    public void setMaximumCharWidth(int maximumCharWidth) {
        this.maximumCharWidth = maximumCharWidth;
    }

    public void setPreferredCharWidth(int preferredCharWidth) {
        this.preferredCharWidth = preferredCharWidth;
    }

    public void setEditKey(KeyStroke editKey) {
        this.editKey = editKey;
    }

    public void setShowEditKey(boolean showEditKey) {
        this.showEditKey = showEditKey;
    }

    public void setFocusable(Boolean focusable) {
        this.focusable = focusable;
    }

    public void setPanelLabelAbove(boolean panelLabelAbove) {
        this.panelLabelAbove = panelLabelAbove;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setClearText(boolean clearText) {
        this.clearText = clearText;
    }

    public void setAskConfirm(boolean askConfirm) {
        this.askConfirm = askConfirm;
    }

    public void setAskConfirmMessage(String askConfirmMessage) {
        this.askConfirmMessage = askConfirmMessage;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public boolean isShowTableFirst() {
        return showTableFirst;
    }

    public boolean isEditOnSingleClick() {
        return editOnSingleClick;
    }

    public boolean isHide() {
        return hide;
    }

    public String getRegexp() {
        return regexp;
    }

    public String getRegexpMessage() {
        return regexpMessage;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public boolean isEchoSymbols() {
        return echoSymbols;
    }

    public String getContentType() {
        return contentType;
    }

    public int getMinimumCharWidth() {
        return minimumCharWidth;
    }

    public int getMaximumCharWidth() {
        return maximumCharWidth;
    }

    public int getPreferredCharWidth() {
        return preferredCharWidth;
    }

    public KeyStroke getEditKey() {
        return editKey;
    }

    public boolean isShowEditKey() {
        return showEditKey;
    }

    public Boolean getFocusable() {
        return focusable;
    }

    public boolean isPanelLabelAbove() {
        return panelLabelAbove;
    }

    public boolean isClearText() {
        return clearText;
    }

    public String getToolTip() {
        return toolTip;
    }

    public boolean isAskConfirm() {
        return askConfirm;
    }

    public String getAskConfirmMessage() {
        return askConfirmMessage;
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.PROPERTY;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        if (isAction) {
            return new ActionPanelView(project);
        } else {
            return new DataPanelView();
        }
    }

    private class ActionPanelView extends JButton {
        private ActionPanelView(Project project) {
            String caption = getEditCaption();
            if (isRedundantString(caption)) {
                setMargin(new Insets(0, 0, 0, 0));
            } else {
                setMargin(new Insets(0, 14, 0, 14));
            }
            setText(caption);

            if (iconPath != null) {
                Icon icon = BaseUtils.loadIcon(project, "/images/" + iconPath);
                if (icon == null) {
                    icon = LSFIcons.ACTION;
                }
                setIcon(icon);
            }

            String toolTipText = !isRedundantString(toolTip) ? toolTip : getEditCaption();
            toolTipText += " (sID: " + sID + ")";
            if (editKey != null) {
                toolTipText += " (" + getKeyStrokeCaption(editKey) + ")";
            }
            setToolTipText(toolTipText);
        }

        @Override
        public Dimension getMinimumSize() {
            return overrideSize(super.getMinimumSize(), minimumSize);
        }

        @Override
        public Dimension getMaximumSize() {
            return overrideSize(super.getMaximumSize(), maximumSize);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension pref = super.getPreferredSize();
            pref.height = 24;
            return overrideSize(pref, preferredSize);
        }
    }
    
    private class DataPanelView extends JBPanel {
        private DataPanelView() {
            SingleCellTable table = new SingleCellTable();

            JBLabel label = new JBLabel(getEditCaption());
            if (panelLabelAbove) {
                label.setHorizontalAlignment(SwingConstants.CENTER);
            }

            setLayout(new DataPanelViewLayout(this, label, table));
            add(label);
            add(table);
        }
    }

    private class DataPanelViewLayout extends CachableLayout {

        private final JBLabel label;
        private final JBTable table;

        public DataPanelViewLayout(JBPanel panel, JBLabel label, JBTable table) {
            super(panel, false);
            this.label = label;
            this.table = table;
        }

        @Override
        protected Dimension layoutSize(Container parent, ComponentSizeGetter sizeGetter) {
            Dimension labelSize = sizeGetter.get(label);
            Dimension tableSize = sizeGetter.get(table);
            int width;
            int height;
            if (panelLabelAbove) {
                width = max(labelSize.width, tableSize.width);
                height = limitedSum(labelSize.height, tableSize.height);
            } else {
                width = limitedSum(8, labelSize.width, tableSize.width);
                height = max(labelSize.height, tableSize.height);
            }

            return new Dimension(width, height);
        }

        @Override
        public void layoutContainer(Container parent) {
            boolean vertical = panelLabelAbove;
            boolean tableFirst = showTableFirst;

            Insets in = parent.getInsets();

            int width = parent.getWidth() - in.left - in.right;
            int height = parent.getHeight() - in.top - in.bottom;

            Dimension labelPref = label.getPreferredSize();
            Dimension tablePref = table.getPreferredSize();

            int tableSpace = width;
            int tableLeft = in.left;
            int tableTop = in.top;
            int tableHeight = height;
            if (vertical) {
                tableHeight -= labelPref.height;
                if (!tableFirst) {
                    tableTop += labelPref.height;
                }
            } else {
                //horizontal
                tableSpace = max(0, tableSpace - 4 - labelPref.width - 4);
                if (!tableFirst) {
                    tableLeft += 4 + labelPref.width + 4;
                }
            }

            int tableWidth = tableSpace;
            if (alignment != FlexAlignment.STRETCH) {
                tableWidth = Math.min(tableSpace, tablePref.width);
                if (alignment == FlexAlignment.TRAILING) {
                    tableLeft += tableSpace - tableWidth;
                } else if (alignment == FlexAlignment.CENTER) {
                    tableLeft += (tableSpace - tableWidth) / 2;
                }
            }

            int labelWidth = vertical ? width : labelPref.width;
            int labelHeight = labelPref.height;
            int labelLeft = in.left;
            int labelTop = in.top;

            if (vertical) {
                if (tableFirst) {
                    labelTop += tableHeight;
                }
            } else {
                labelTop += max(0, height - labelHeight) / 2;
                labelLeft += tableFirst ? 4 + tableSpace + 4 : 4;
            }

            label.setBounds(labelLeft, labelTop, labelWidth, labelHeight);
            table.setBounds(tableLeft, tableTop, tableWidth, tableHeight);
        }
    }
}
