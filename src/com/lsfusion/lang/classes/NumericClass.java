package com.lsfusion.lang.classes;

import java.text.NumberFormat;

public class NumericClass extends DoubleClass {

    private final ExtInt length;
    private final ExtInt precision;

    public NumericClass(int length, int precision) {
        this(new ExtInt(length), new ExtInt(precision));
    }

    public NumericClass(ExtInt length, ExtInt precision) {
        this.length = length;
        this.precision = precision;
    }

    public NumberFormat getDefaultFormat() {
        NumberFormat format = super.getDefaultFormat();
        format.setMaximumIntegerDigits(getWhole());
        format.setMaximumFractionDigits(getPrecision());
        return format;
    }

    @Override
    int getWhole() {
        return getLength() - getPrecision();
    }

    int getLength() {
        return length.isUnlimited() ? 127 : length.value;
    }

    @Override
    int getPrecision() {
        return precision.isUnlimited() ? 32 : precision.value;
    }


    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof NumericClass && length == ((NumericClass) obj).length && precision == ((NumericClass) obj).precision);
    }

    @Override
    public int hashCode() {
        return length.value * 31 + precision.value;
    }

    private boolean isUnlimited() {
        return length.isUnlimited();
    }

    public String getName() {
        return "NUMERIC" + (isUnlimited() ? "" : ("[" + length + "," + precision + "]"));
    }

    @Override
    public String getCaption() {
        return "Numeric" + (isUnlimited() ? "" : ("[" + length + "," + precision + "]"));
    }

    @Override
    public String getCanonicalName() {
        String userSID = super.getCanonicalName();
        return userSID.contains("_") ? (userSID.replaceFirst("_", "[").replaceFirst("_", ",") + "]") : userSID;
    }

    @Override
    public boolean isAssignable(LSFClassSet set) {
        return !(set instanceof NumericClass && !isUnlimited() && (((NumericClass) set).isUnlimited() || length.value < ((NumericClass) set).length.value || precision.value < ((NumericClass) set).precision.value)) && super.isAssignable(set);
    }
}
