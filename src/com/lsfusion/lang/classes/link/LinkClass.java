package com.lsfusion.lang.classes.link;

import com.lsfusion.lang.classes.DataClass;

public abstract class LinkClass extends DataClass {
    @Override
    public String getMask() {
        return "1234567";
    }

    @Override
    public boolean isFlex() {
        return false;
    }
}