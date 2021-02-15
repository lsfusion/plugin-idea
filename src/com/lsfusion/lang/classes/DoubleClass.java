package com.lsfusion.lang.classes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class DoubleClass extends IntegralClass {

    public NumberFormat getDefaultFormat() {
        NumberFormat format = super.getDefaultFormat();
        format.setMaximumFractionDigits(10);

        DecimalFormat decimalFormat = (DecimalFormat) format;
        DecimalFormatSymbols dfs = decimalFormat.getDecimalFormatSymbols();
        if (dfs.getGroupingSeparator() != '.') {
            dfs.setDecimalSeparator('.');
        }
        decimalFormat.setDecimalFormatSymbols(dfs);

        return format;
    }

    public final static DoubleClass instance = new DoubleClass();

    int getWhole() {
        return 99999;
    }

    int getPrecision() {
        return 99999;
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    public int hashCode() {
        return 6;
    }

    public String getName() {
        return "DOUBLE";
    }

    @Override
    public String getCaption() {
        return "Double";
    }

    public ExtInt getCharLength() {
        return new ExtInt(20); // ? double has 15-16 precise digits + sign + dot
    }
}
