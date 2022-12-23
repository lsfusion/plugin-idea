package com.lsfusion.design.model;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.lsfusion.design.ui.*;

import javax.swing.*;
import java.awt.*;

public class DataPanelView extends JBPanel {
    private PropertyDrawView property;
    
    private boolean tableFirst;

    public DataPanelView(PropertyDrawView property) {
        this.property = property;
        
        tableFirst = property.getNotNullPanelCaptionLast();

        setLayout(new FlexLayout(this, property.panelCaptionVertical, Alignment.CENTER));

        JBLabel label = new JBLabel(property.getEditCaption());
        if (!property.panelCaptionVertical) {
            label.setBorder(BorderFactory.createEmptyBorder(0,
                    tableFirst ? 2 : 0,
                    0,
                    tableFirst ? 0 : 2));
        }

        SingleCellTable table = new SingleCellTable();
        table.setPreferredSize(new Dimension(property.getValueWidth(this), property.getValueHeight(this)));
        
        if (property.font != null) {
            table.setFont(property.font.deriveFrom(table));
        }

        if (!tableFirst) {
            add(label, new FlexConstraints(property.getNotNullPanelCaptionAlignment(), 0));
        }
        
        add(table, new FlexConstraints(FlexAlignment.STRETCH, 1));
        
        if (tableFirst) {
            add(label, new FlexConstraints(property.getNotNullPanelCaptionAlignment(), 0));
        }

        label.setToolTipText(property.getTooltipText(property.getCaption()));
    }

}


