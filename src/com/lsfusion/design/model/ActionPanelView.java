package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.KeyStrokes;
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

        if (property.iconPath != null) {
            Icon icon = BaseUtils.loadIcon(project, "/images/" + property.iconPath);
            if (icon == null) {
                icon = LSFIcons.ACTION;
            }
            setIcon(icon);
        }

        if (property.font != null) {
            setFont(property.font.deriveFrom(this));
        }

        setToolTip(property.getCaption());
    }

    public void setToolTip(String caption) {
        String toolTip = !BaseUtils.isRedundantString(property.toolTip) ? property.toolTip : caption;
        toolTip += " (sID: " + property.getDisplaySID() + ")";
        if (property.editKey != null) {
            toolTip += " (" + KeyStrokes.getKeyStrokeCaption(property.editKey) + ")";
        }
        setToolTipText(toolTip);
    }

    @Override
    public Dimension getMinimumSize() {
        return overrideSize(super.getMinimumSize(), property.minimumSize);
    }

    @Override
    public Dimension getMaximumSize() {
        return overrideSize(super.getMaximumSize(), property.maximumSize);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension pref = super.getPreferredSize();
        if (property.preferredSize == null && property.font == null) {
            pref.height = 24;
        }
        return overrideSize(pref, property.preferredSize);
    }
}
