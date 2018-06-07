package com.lsfusion.refactoring;

public class CompoundNameUtils {
    public static String createName(String namespace, String name) {
        return namespace + "." + name; 
    }
    
    public static String createStaticObjectName(String classNamespace, String className, String objectName) {
        return classNamespace + "." + className + "." + objectName;
    }
}
