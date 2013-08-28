package com.simpleplugin.classes;

public abstract class DataClass implements LSFClassSet {

    public static final DataClass BOOLEAN = new SimpleDataClass("BOOLEAN");

    public static final DataClass STRUCT = new SimpleDataClass("STRUCT");
    
    public abstract DataClass or(DataClass compClass);
}
