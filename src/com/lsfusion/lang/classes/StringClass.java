package com.lsfusion.lang.classes;

import com.lsfusion.util.BaseUtils;

import static java.lang.Math.pow;
import static java.lang.Math.round;

public class StringClass extends AStringClass {

    public StringClass(boolean blankPadded, boolean caseInsensitive, ExtInt length) {
        super(blankPadded, caseInsensitive, length);
    }

    public DataClass op(DataClass compClass, boolean or, boolean string) {
        if (!(compClass instanceof StringClass)) return null;
        if(compClass instanceof TextClass)
            return compClass.op(this, or, string);

        assert or || !string;
        StringClass stringClass = (StringClass) compClass;
        return new StringClass(BaseUtils.cmp(blankPadded, stringClass.blankPadded, or && !string), BaseUtils.cmp(caseInsensitive, stringClass.caseInsensitive, or),
                string ? length.sum(stringClass.length) : length.cmp(stringClass.length, or));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof StringClass && blankPadded == ((StringClass) obj).blankPadded && caseInsensitive == ((StringClass) obj).caseInsensitive && length.equals(((StringClass) obj).length));
    }

    public int hashCode() {
        return (length.hashCode() * 31 + (blankPadded ? 1 : 0)) * 31 + (caseInsensitive ? 1 : 0);
    }

    public String getName() {
        return (blankPadded ? "BP" : "") + (caseInsensitive ? "I" : "") + "STRING" + (length.isUnlimited() ? "" : "[" + length.getValue() + "]");
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
    public String getCanonicalName() {
        String userSID = super.getCanonicalName();
        if (length == ExtInt.UNLIMITED || !userSID.contains("_")) {
            return userSID;
        } else {
            return userSID.replaceFirst("_", "[") + "]";
        }        
    }

    public StringClass extend(int times) {
        if(length.isUnlimited())
            return this;
        return new StringClass(blankPadded, caseInsensitive, new ExtInt(BaseUtils.min(length.getValue() * times, 4000)));
    }
}
