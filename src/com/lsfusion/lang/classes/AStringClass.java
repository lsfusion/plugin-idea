package com.lsfusion.lang.classes;

public abstract class AStringClass extends DataClass {

    public final boolean blankPadded;
    public final boolean caseInsensitive;
    public final ExtInt length;

    public AStringClass(boolean blankPadded, boolean caseInsensitive, ExtInt length) {
        this.blankPadded = blankPadded;
        this.caseInsensitive = caseInsensitive;
        this.length = length;
    }

    @Override
    public String getCaption() {
        return "String" + (caseInsensitive ? " case insensitive" : "") + (blankPadded ? " blankpadded" : "") + "(" + length + ")";
    }

    @Override
    public boolean fixedSize() {
        return false;
    }

    @Override
    public boolean isAssignable(LSFClassSet set) {
        return !(set instanceof AStringClass && length.less(((AStringClass) set).length)) && super.isAssignable(set);
    }

    @Override
    public boolean isFlex() {
        return true;
    }

    @Override
    public ExtInt getCharLength() {
        return length;
    }
}