package com.lsfusion.lang.classes;

import com.lsfusion.design.model.PropertyDrawView;

import java.awt.*;

public class ColorClass extends DataClass {

    public final static ColorClass instance = new ColorClass();

    @Override
    public String getName() {
        return "COLOR";
    }

    @Override
    public String getCaption() {
        return "Color";
    }

    @Override
    public int getDefaultWidth(FontMetrics fontMetrics, PropertyDrawView propertyDraw) {
        return 40;
    }
}
