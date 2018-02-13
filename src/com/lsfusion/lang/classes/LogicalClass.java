package com.lsfusion.lang.classes;

import com.lsfusion.design.model.PropertyDrawView;

import java.awt.*;

public class LogicalClass extends DataClass {

    public final static LogicalClass instance = new LogicalClass();

    @Override
    public String getName() {
        return "BOOLEAN";
    }

    @Override
    public String getCaption() {
        return "Логическое значение";
    }

    @Override
    public int getDefaultWidth(FontMetrics fontMetrics, PropertyDrawView propertyDraw) {
        return 25;
    }
}
