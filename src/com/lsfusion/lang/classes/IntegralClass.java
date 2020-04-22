package com.lsfusion.lang.classes;

import com.lsfusion.util.BaseUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;

import static java.lang.Math.pow;
import static java.lang.Math.round;

public abstract class IntegralClass extends FormatClass {

    public NumberFormat getDefaultFormat() {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(true);
        return format;
    }

    @Override
    public NumberFormat createUserFormat(String pattern) {
        return new DecimalFormat(pattern);
    }

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

        if(!(this instanceof NumericClass) && !(compClass instanceof NumericClass))
            return op(compClass, true, false);

        int whole;
        int precision;
        if(mult) {
            whole = getWhole() + compClass.getWhole();
            precision = getPrecision() + compClass.getPrecision();
        } else {
            whole = getWhole() + compClass.getPrecision();
//            precision = getPrecision() + compClass.getWhole();
            precision = NumericClass.MAX_PRECISION;
        }
        return new NumericClass(whole + precision, precision);
    }

    @Override
    public int getDefaultCharWidth() {
        int lengthValue = this instanceof DoubleClass ? 10 : getWhole() + getPrecision();
        return lengthValue <= 6 ? lengthValue : (int) round(6 + pow(lengthValue - 6, 0.7));
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
