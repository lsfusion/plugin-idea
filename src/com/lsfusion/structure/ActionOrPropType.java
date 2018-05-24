package com.lsfusion.structure;

public enum ActionOrPropType {
    PROP, ACTION, ACTION_OR_PROP;
    
    public boolean isProp() {
        return this == PROP || this == ACTION_OR_PROP;
    }

    public boolean isAction() {
        return this == ACTION || this == ACTION_OR_PROP;
    }    
}
