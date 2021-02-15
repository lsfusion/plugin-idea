package com.lsfusion.lang.classes;

import com.lsfusion.design.model.PropertyDrawView;

import java.awt.*;

public abstract class FileClass extends DataClass {

    @Override
    public int getDefaultWidth(FontMetrics font, PropertyDrawView propertyDraw) {
        return 18;
    }

    @Override
    public ExtInt getCharLength() {
        return ExtInt.UNLIMITED;
    }
}
