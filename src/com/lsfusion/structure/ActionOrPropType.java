package com.lsfusion.structure;

public enum ActionOrPropType {
    PROP, ACTION, ACTION_OR_PROP,
    // what can go into an expression: the properties and the actions with RETURN
    PROP_OR_VALUE_ACTION;

    public boolean isProp() {
        return this == PROP || this == ACTION_OR_PROP || this == PROP_OR_VALUE_ACTION;
    }

    public boolean isAction() {
        return this == ACTION || this == ACTION_OR_PROP || this == PROP_OR_VALUE_ACTION;
    }

    public boolean isValueActionsOnly() {
        return this == PROP_OR_VALUE_ACTION;
    }
}
