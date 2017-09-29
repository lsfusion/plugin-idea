package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.FlexAlignment;

import javax.swing.*;
import java.util.List;

public class ClassChooserView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("visible")
    );

    public boolean visible = true;
    private ObjectView objectView;

    public ClassChooserView() {
        this("");
    }

    public ClassChooserView(String sID) {
        super(sID);
        setFlex(0.2);
        setAlignment(FlexAlignment.STRETCH);
    }

    public ClassChooserView(ObjectView objectView) {
        this.objectView = objectView;
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

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.CLASS_CHOOSER;
    }
}
