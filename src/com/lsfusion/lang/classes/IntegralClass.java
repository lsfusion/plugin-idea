package com.lsfusion.lang.classes;

import com.lsfusion.util.BaseUtils;

import java.util.Collection;

public abstract class IntegralClass extends DataClass {

    abstract int getWhole();

    abstract int getPrecision();

    @Override
    public IntegralClass op(DataClass compClass, boolean or, boolean string) {
        if (!(compClass instanceof IntegralClass)) return null;

        IntegralClass integralClass = (IntegralClass) compClass;
        if (getWhole() >= integralClass.getWhole() && getPrecision() >= integralClass.getPrecision())
            return or ? this : integralClass;
        if (getWhole() <= integralClass.getWhole() && getPrecision() <= integralClass.getPrecision())
            return or ? integralClass : this;
        int whole = BaseUtils.cmp(getWhole(), integralClass.getWhole(), or);
        int precision = BaseUtils.cmp(getPrecision(), integralClass.getPrecision(), or);
        return new NumericClass(whole + precision, precision);
    }

    public DataClass scale(IntegralClass compClass, boolean mult) {

        if(!(this instanceof NumericClass) || !(compClass instanceof NumericClass))
            return op(compClass, true, false);

        int whole;
        int precision;
        if(mult) {
            whole = getWhole() + compClass.getWhole();
            precision = getPrecision() + compClass.getPrecision();
        } else {
            whole = getWhole() + compClass.getPrecision();
            precision = getPrecision() + compClass.getWhole();
        }
        return new NumericClass(whole + precision, precision);
    }

    @Override
    public String getMask() {
        return "99 999 999";
    }

    @Override
    public Collection<String> getExtraNames() {
        return BaseUtils.toList("Result", "Quantity");
    }

    @Override
    public boolean fixedSize() {
        return false;
    }

    @Override
    public boolean isFlex() {
        return true;
    }
}
