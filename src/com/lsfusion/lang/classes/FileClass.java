package com.lsfusion.lang.classes;

import com.lsfusion.design.model.PropertyDrawView;

import java.awt.*;

public abstract class FileClass extends DataClass {

    @Override
    public int getDefaultHeight(FontMetrics font) {
        return 18;
    }

    @Override
    public int getDefaultWidth(FontMetrics font, PropertyDrawView propertyDraw) {
        return 18;
    }
}
