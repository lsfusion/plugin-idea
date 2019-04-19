package com.lsfusion.lang.classes;

import com.lsfusion.util.BaseUtils;

import java.awt.*;

import static java.lang.Math.pow;
import static java.lang.Math.round;

public class StringClass extends DataClass {

    public final boolean blankPadded;
    public final boolean caseInsensitive;
    public final ExtInt length;

    public StringClass(boolean blankPadded, boolean caseInsensitive, ExtInt length) {
        this.blankPadded = blankPadded;
        this.caseInsensitive = caseInsensitive;
        this.length = length;
    }

    public DataClass op(DataClass compClass, boolean or, boolean string) {
        if (!(compClass instanceof StringClass)) return null;

        assert or || !string;
        StringClass stringClass = (StringClass) compClass;
        return new StringClass(BaseUtils.cmp(blankPadded, stringClass.blankPadded, or && !string), BaseUtils.cmp(caseInsensitive, stringClass.caseInsensitive, or),
                length.cmp(stringClass.length, or));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof StringClass && blankPadded == ((StringClass) obj).blankPadded && caseInsensitive == ((StringClass) obj).caseInsensitive && length.equals(((StringClass) obj).length));
    }

    public int hashCode() {
        return (length.hashCode() * 31 + (blankPadded ? 1 : 0)) * 31 + (caseInsensitive ? 1 : 0);
    }

    public String getName() {
        return (blankPadded ? "" : "VAR") + (caseInsensitive ? "I" : "") + "STRING" + (length.isUnlimited() ? "" : "[" + length.getValue() + "]");
    }

    @Override
    public String getCaption() {
        return "Строка" + (caseInsensitive ? " без регистра" : "") + (blankPadded ? " с паддингом" : "") + "(" + length + ")";
    }

    @Override
    public int getDefaultCharWidth() {
        if(length.isUnlimited()) {
            return 15;
        } else {
            int lengthValue = length.getValue();
            return lengthValue <= 12 ? lengthValue : (int) round(12 + pow(lengthValue - 12, 0.7));
        }
    }

    @Override
    public String toString() {
        return "STRING";
    }

    @Override
    public boolean fixedSize() {
        return false;
    }

    @Override
    public String getCanonicalName() {
        String userSID = super.getCanonicalName();
        if (length == ExtInt.UNLIMITED || !userSID.contains("_")) {
            return userSID;
        } else {
            return userSID.replaceFirst("_", "[") + "]";
        }        
    }

    @Override
    public boolean isAssignable(LSFClassSet set) {
        return !(set instanceof StringClass && length.less(((StringClass) set).length)) && super.isAssignable(set);
    }

    @Override
    public boolean isFlex() {
        return true;
    }

    public StringClass extend(int times) {
        if(length.isUnlimited())
            return this;
        return new StringClass(blankPadded, caseInsensitive, new ExtInt(BaseUtils.min(length.getValue() * times, 4000)));
    }
}
