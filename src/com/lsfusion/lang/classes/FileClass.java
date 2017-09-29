package com.lsfusion.lang.classes;

public abstract class FileClass extends DataClass {
    @Override
    public String getPreferredMask() {
        return "1234567";
    }

    @Override
    public boolean isFlex() {
        return false;
    }
}
