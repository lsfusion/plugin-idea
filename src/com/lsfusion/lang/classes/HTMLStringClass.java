package com.lsfusion.lang.classes;

public class HTMLStringClass extends AStringClass {

    public HTMLStringClass() {
        super(false, false, ExtInt.UNLIMITED);
    }

    public String getName() {
        return "HTML";
    }

    @Override
    public String toString() {
        return "HTML";
    }

    @Override
    public DataClass op(DataClass compClass, boolean or, boolean string) {
        return compClass instanceof HTMLStringClass ? this : super.op(compClass, or, string);
    }
}
