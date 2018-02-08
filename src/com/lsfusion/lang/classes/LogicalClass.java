package com.lsfusion.lang.classes;

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
    public int getWidth(int minCharWidth, FontMetrics fontMetrics) {
        return 25;
    }

    @Override
    public String getMask() {
        throw new UnsupportedOperationException();
    }
}
