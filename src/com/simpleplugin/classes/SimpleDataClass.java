package com.simpleplugin.classes;

public class SimpleDataClass extends DataClass {
    
    private String dataName;

    public SimpleDataClass(String dataName) {
        this.dataName = dataName;
    }

    public DataClass or(DataClass compClass) {
        if(compClass.equals(this))
            return this;
        return null;
    }
}
