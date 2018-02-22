package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.lsfusion.LSFIcons;
import com.lsfusion.util.BaseUtils;

import javax.swing.*;
import java.awt.*;

import static com.lsfusion.util.BaseUtils.isRedundantString;
import static com.lsfusion.util.BaseUtils.overrideSize;

public class ActionPanelView extends JButton {
    private PropertyDrawView property;

    public ActionPanelView(Project project, PropertyDrawView property) {
        this.property = property;

        if (property.showCaption) {
            String caption = property.getEditCaption();
            if (isRedundantString(caption)) {
                setMargin(new Insets(0, 0, 0, 0));
            } else {
                setMargin(new Insets(0, 14, 0, 14));
            }
            setText(caption);
        }

        if (property.imagePath != null) {
            Icon icon = BaseUtils.loadIcon(project, "/images/" + property.imagePath);
            if (icon == null) {
                icon = LSFIcons.ACTION;
            }
            setIcon(icon);
        }

        if (property.font != null) {
            setFont(property.font.deriveFrom(this));
        }

        setToolTipText(property.getTooltipText(property.getCaption()));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension pref = super.getPreferredSize();
        if (property.valueSize == null && property.font == null) {
            pref.height = 24;
        }
        return overrideSize(super.getPreferredSize(), property.valueSize); // так как caption и есть значение видимо
    }
}
