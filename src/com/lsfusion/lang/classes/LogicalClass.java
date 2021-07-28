package com.lsfusion.lang.classes;

import com.lsfusion.design.model.PropertyDrawView;

import java.awt.*;

public class LogicalClass extends DataClass {

    public final static LogicalClass instance = new LogicalClass(false);

    public final static LogicalClass threeStateInstance = new LogicalClass(true);

    boolean threeState;

    public LogicalClass(boolean threeState) {
        this.threeState = threeState;
    }

    @Override
    public DataClass op(DataClass compClass, boolean or, boolean string) {
        return compClass instanceof LogicalClass && threeState == ((LogicalClass) compClass).threeState ? this : null;
    }

    @Override
    public String getName() {
        return threeState ? "TBOOLEAN" : "BOOLEAN";
    }

    @Override
    public String getCaption() {
        return threeState ? "TBoolean" : "Boolean";
    }

    @Override
    public int getDefaultWidth(FontMetrics fontMetrics, PropertyDrawView propertyDraw) {
        return 25;
    }

    @Override
    public ExtInt getCharLength() {
        return new ExtInt(1);
    }
}
