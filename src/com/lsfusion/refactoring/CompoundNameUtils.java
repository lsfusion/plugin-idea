package com.lsfusion.refactoring;

public class CompoundNameUtils {
    public static String createName(String namespace, String name) {
        return namespace + "." + name; 
    }
}
