package com.lsfusion.lang.classes;

import com.lsfusion.util.BaseUtils;

import java.awt.*;

public class StringClass extends DataClass {

    public final boolean blankPadded;
    public final boolean caseInsensitive;
    public final boolean rich;
    public final ExtInt length;

    private String minimumMask;
    private String preferredMask;

    public StringClass(boolean blankPadded, boolean caseInsensitive, ExtInt length) {
        this(blankPadded, caseInsensitive, false, length);
    }

    public StringClass(boolean blankPadded, boolean caseInsensitive, boolean rich, ExtInt length) {
        this.blankPadded = blankPadded;
        this.caseInsensitive = caseInsensitive;
        this.rich = rich;
        this.length = length;

        if (length.isUnlimited()) {
            minimumMask = "999 999";
            preferredMask = "9 999 999";
        } else {
            int lengthValue = length.getValue();
            minimumMask = BaseUtils.replicate('0', lengthValue <= 3 ? lengthValue : (int) Math.round(Math.pow(lengthValue, 0.7)));
            preferredMask = BaseUtils.replicate('0', lengthValue <= 20 ? lengthValue : (int) Math.round(Math.pow(lengthValue, 0.8)));
        }
    }

    public DataClass op(DataClass compClass, boolean or, boolean string) {
        if (!(compClass instanceof StringClass)) return null;

        assert or || !string;
        StringClass stringClass = (StringClass) compClass;
        return new StringClass(BaseUtils.cmp(blankPadded, stringClass.blankPadded, or && !string), BaseUtils.cmp(caseInsensitive, stringClass.caseInsensitive, or),
                BaseUtils.cmp(rich, stringClass.rich, or), length.cmp(stringClass.length, or));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof StringClass && blankPadded == ((StringClass) obj).blankPadded && caseInsensitive == ((StringClass) obj).caseInsensitive && length.equals(((StringClass) obj).length));
    }

    public int hashCode() {
        return (length.hashCode() * 31 + (blankPadded ? 1 : 0)) * 31 + (caseInsensitive ? 1 : 0);
    }

    public String getName() {
        return length.isUnlimited() ? (rich ? "RICHTEXT" : "TEXT") : (blankPadded ? "" : "VAR") + (caseInsensitive ? "I" : "") + "STRING[" + length.getValue() + "]";
    }

    @Override
    public String getCaption() {
        return "Строка" + (caseInsensitive ? " без регистра" : "") + (blankPadded ? " с паддингом" : "") + "(" + length + ")";
    }

    @Override
    public String getMinimumMask() {
        return minimumMask;
    }

    @Override
    public String getPreferredMask() {
        return preferredMask;
    }

    @Override
    public int getPreferredHeight(FontMetrics fontMetrics) {
        if (length.isUnlimited())
            return 4 * (fontMetrics.getHeight() + 1);
        return super.getPreferredHeight(fontMetrics);
    }

    @Override
    public int getMaximumHeight(FontMetrics fontMetrics) {
        if (length.isUnlimited())
            return Integer.MAX_VALUE;
        return super.getPreferredHeight(fontMetrics);
    }

    @Override
    public int getPreferredWidth(int prefCharWidth, FontMetrics fontMetrics) {
        if (length.isUnlimited())
            return fontMetrics.charWidth('0') * 25;
        return super.getPreferredWidth(prefCharWidth, fontMetrics);
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
        if (length == ExtInt.UNLIMITED) {
            return userSID;
        } else {
            return userSID.replaceFirst("_", "[") + "]";
        }        
    }
}
