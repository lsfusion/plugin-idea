package com.simpleplugin.classes;

import com.simpleplugin.BaseUtils;

public abstract class IntegralClass extends DataClass {

    abstract int getWhole();
    abstract int getPrecision();

    @Override
    public DataClass or(DataClass compClass) {
        if(!(compClass instanceof IntegralClass)) return null;

        IntegralClass integralClass = (IntegralClass)compClass;
        if(getWhole()>=integralClass.getWhole() && getPrecision()>=integralClass.getPrecision())
            return this;
        if(getWhole()<=integralClass.getWhole() && getPrecision()<=integralClass.getPrecision())
            return integralClass;
        int whole = BaseUtils.max(getWhole(), integralClass.getWhole());
        int precision = BaseUtils.max(getPrecision(), integralClass.getPrecision());
        return new NumericClass(whole+precision, precision);
    }
}
