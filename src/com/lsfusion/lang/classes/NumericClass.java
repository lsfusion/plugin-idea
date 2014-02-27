package com.lsfusion.classes;

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
}
