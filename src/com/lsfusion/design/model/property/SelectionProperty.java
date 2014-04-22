package com.lsfusion.design.model.property;

import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.lang.classes.LogicalClass;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;

public class SelectionProperty extends PropertyDrawEntity {
    public SelectionProperty(String alias, String sIDPostfix, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias != null ? alias : "SelectionProperty_" + sIDPostfix, null, commonFormOptions, propertyFormOptions, form);
        caption = "Отметить";
        baseClass = new LogicalClass();
    }
}
