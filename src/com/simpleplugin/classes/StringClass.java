package com.simpleplugin.classes;

public class StringClass extends DataClass {

    public final boolean blankPadded;
    public final boolean caseInsensitive;
    public final ExtInt length;

    public StringClass(boolean blankPadded, boolean caseInsensitive, ExtInt length) {
        this.blankPadded = blankPadded;
        this.caseInsensitive = caseInsensitive;
        this.length = length;
    }
    
    public DataClass or(DataClass compClass) {
        if (!(compClass instanceof StringClass)) return null;

        StringClass stringClass = (StringClass) compClass;
        return new StringClass(blankPadded || stringClass.blankPadded, caseInsensitive || stringClass.caseInsensitive, length.max(stringClass.length));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof StringClass && blankPadded == ((StringClass)obj).blankPadded && caseInsensitive == ((StringClass)obj).caseInsensitive && length.equals(((StringClass)obj).length));
    }

    public int hashCode() {
        return (length.hashCode() * 31 + (blankPadded ? 1 : 0)) * 31 + (caseInsensitive ? 1 : 0);  
    }
}
