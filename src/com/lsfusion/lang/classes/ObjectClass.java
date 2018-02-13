package com.lsfusion.lang.classes;

import com.lsfusion.design.model.PropertyDrawView;

import java.awt.*;

public class ObjectClass extends DataClass {
    @Override
    public String getCaption() {
        return "Объект";
    }

    @Override
    public String getName() {
        return "OBJECT";
    }

    @Override
    public int getDefaultWidth(FontMetrics fontMetrics, PropertyDrawView propertyDraw) {
        return getFullWidthString("0000000", fontMetrics);
    }

    @Override
    public int getDefaultHeight(FontMetrics fontMetrics) {
        return fontMetrics.getHeight() + 1;
    }
}
