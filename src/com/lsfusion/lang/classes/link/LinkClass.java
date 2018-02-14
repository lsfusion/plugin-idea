package com.lsfusion.lang.classes.link;

import com.lsfusion.design.model.PropertyDrawView;
import com.lsfusion.lang.classes.DataClass;

import java.awt.*;

public abstract class LinkClass extends DataClass {

    @Override
    public int getDefaultWidth(FontMetrics font, PropertyDrawView propertyDraw) {
        return 18;
    }
}