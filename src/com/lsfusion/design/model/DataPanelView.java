package com.lsfusion.design.model;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;
import com.lsfusion.design.ui.*;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.max;

public class DataPanelView extends JBPanel {
    private int labelWidth = -1;
    private PropertyDrawView property;

    public DataPanelView(PropertyDrawView property) {
        this.property = property;

        setLayout(new FlexLayout(this, property.panelCaptionAbove, Alignment.CENTER));

        

        JBLabel label = new JBLabel(property.getEditCaption());
        if (!property.panelCaptionAbove) {
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
        }

        SingleCellTable table = new SingleCellTable();
        table.setPreferredSize(new Dimension(property.getValueWidth(this), property.getValueHeight(this)));
        
        if (property.font != null) {
            table.setFont(property.font.deriveFrom(table));
        }

        add(label, new FlexConstraints(FlexAlignment.CENTER, 0));
        add(table, new FlexConstraints(FlexAlignment.STRETCH, 1));

        label.setToolTipText(property.getTooltipText(property.getCaption()));
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
            if (property.getAlignment() != FlexAlignment.STRETCH) {
                tableWidth = Math.min(tableSpace, tablePref.width);
                if (property.getAlignment() == FlexAlignment.TRAILING) {
                    tableLeft += tableSpace - tableWidth;
                } else if (property.getAlignment() == FlexAlignment.CENTER) {
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


