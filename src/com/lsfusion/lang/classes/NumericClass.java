package com.lsfusion.lang.classes;

public class NumericClass extends IntegralClass {

    private final int length;
    private final int precision;

    public NumericClass(int length, int precision) {
        this.length = length;
        this.precision = precision;
    }

    @Override
    int getWhole() {
        return length - precision;
    }

    @Override
    int getPrecision() {
        return precision;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof NumericClass && length == ((NumericClass) obj).length && precision == ((NumericClass) obj).precision);
    }

    @Override
    public int hashCode() {
        return length * 31 + precision;
    }

    public String getName() {
        return "NUMERIC[" + length + "," + precision + "]";
    }

    @Override
    public String getCaption() {
        return "Число" + '[' + length + ',' + precision + ']';
    }

    @Override
    public String getCanonicalName() {
        String userSID = super.getCanonicalName();
        return userSID.contains("_") ? (userSID.replaceFirst("_", "[").replaceFirst("_", ",") + "]") : userSID;
    }

    @Override
    public boolean isAssignable(LSFClassSet set) {
        return !(set instanceof NumericClass && (length < ((NumericClass) set).length || precision < ((NumericClass) set).precision)) && super.isAssignable(set);
    }
}
