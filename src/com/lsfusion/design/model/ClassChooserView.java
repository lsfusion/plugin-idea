package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.properties.ReflectionProperty;

import javax.swing.*;
import java.util.List;

public class ClassChooserView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("visible")
    );

    public boolean visible = true;

    public ClassChooserView() {
    }

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getCaption() {
        return "Class chooser";
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.CLASS_CHOOSER;
    }
}
