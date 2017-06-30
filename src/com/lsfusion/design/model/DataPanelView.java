package com.lsfusion.design.model;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;
import com.lsfusion.design.ui.CachableLayout;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.SingleCellTable;

import javax.swing.*;
import java.awt.*;

import static com.lsfusion.util.BaseUtils.overrideSize;
import static java.lang.Math.max;

public class DataPanelView extends JBPanel implements HasLabel {
    private int labelWidth = -1;
    private PropertyDrawView property;

    public DataPanelView(PropertyDrawView property) {
        this.property = property;

        SingleCellTable table = new SingleCellTable();

        JBLabel label = new JBLabel(property.getEditCaption());
        if (property.panelCaptionAbove) {
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        table.setMinimumSize(overrideSize(table.getMinimumSize(), property.getMinimumValueSize(this)));
        table.setMaximumSize(overrideSize(table.getMaximumSize(), property.getMaximumValueSize(this)));
        table.setPreferredSize(overrideSize(table.getPreferredSize(), property.getPreferredValueSize(this)));
        if (property.font != null) {
            table.setFont(property.font.deriveFrom(table));
        }

        setLayout(new DataPanelViewLayout(this, label, table));
        add(label);
        add(table);

        label.setToolTipText(property.getTooltipText(property.getCaption()));
    }

    @Override
    public void setLabelWidth(int width) {
        labelWidth = width;
    }

    private class DataPanelViewLayout extends CachableLayout {
        private final JBLabel label;
        private final JBTable table;

        public DataPanelViewLayout(DataPanelView panel, JBLabel label, JBTable table) {
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
            if (property.panelCaptionAbove) {
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
            boolean vertical = property.panelCaptionAbove;
            boolean tableFirst = property.panelCaptionAfter;

            Insets in = parent.getInsets();

            int width = parent.getWidth() - in.left - in.right;
            int height = parent.getHeight() - in.top - in.bottom;

            Dimension labelPref = label.getPreferredSize();
            Dimension tablePref = table.getPreferredSize();

            if (!tableFirst && labelWidth != -1) {
                labelPref.width = labelWidth;
            }

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
            if (property.alignment != FlexAlignment.STRETCH) {
                tableWidth = Math.min(tableSpace, tablePref.width);
                if (property.alignment == FlexAlignment.TRAILING) {
                    tableLeft += tableSpace - tableWidth;
                } else if (property.alignment == FlexAlignment.CENTER) {
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


