package com.lsfusion.design.model.entity;

public enum FormSessionScope {
    NEWSESSION, OLDSESSION, NESTEDSESSION;

    public static FormSessionScope forEditFormActionName(String name) {
        if ("EDITFORM".equals(name)) {
            return NEWSESSION;
        } else if ("EDITSESSIONFORM".equals(name)) {
            return OLDSESSION;
        } else if ("EDITNESTEDFORM".equals(name)) {
            return NESTEDSESSION;
        }
        throw new IllegalStateException("incorrect EDITFORM action name"); 
    }
    
    
}
