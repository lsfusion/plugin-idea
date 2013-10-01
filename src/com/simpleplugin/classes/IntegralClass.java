package com.simpleplugin.classes;

import com.simpleplugin.BaseUtils;

public abstract class IntegralClass extends DataClass {

    abstract int getWhole();
    abstract int getPrecision();

    @Override
    public DataClass op(DataClass compClass, boolean or) {
        if(!(compClass instanceof IntegralClass)) return null;

        IntegralClass integralClass = (IntegralClass)compClass;
        if(getWhole()>=integralClass.getWhole() && getPrecision()>=integralClass.getPrecision())
            return or ? this : integralClass;
        if(getWhole()<=integralClass.getWhole() && getPrecision()<=integralClass.getPrecision())
            return or ? integralClass : this;
        int whole = BaseUtils.cmp(getWhole(), integralClass.getWhole(), or);
        int precision = BaseUtils.cmp(getPrecision(), integralClass.getPrecision(), or);
        return new NumericClass(whole+precision, precision);
    }
}
