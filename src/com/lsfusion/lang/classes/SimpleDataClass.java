package com.lsfusion.lang.classes;

public class SimpleDataClass extends DataClass {

    private String dataName;

    public SimpleDataClass(String dataName) {
        this.dataName = dataName;
    }

    public DataClass op(DataClass compClass, boolean or) {
        if (compClass.equals(this))
            return this;
        return null;
    }

    @Override
    public String getPreferredMask() {
        return "1234567";
    }

    public boolean equals(Object o) {
        return this == o || o instanceof SimpleDataClass && dataName.equals(((SimpleDataClass) o).dataName);
    }

    public int hashCode() {
        return dataName.hashCode();
    }

    public String getName() {
        return dataName;
    }

    @Override
    public String getCaption() {
        return null;
    }
}