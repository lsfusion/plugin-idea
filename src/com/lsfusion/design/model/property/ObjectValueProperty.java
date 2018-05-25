package com.lsfusion.design.model.property;

import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;

public class ObjectValueProperty extends PropertyDrawEntity {
    public ObjectValueProperty(String alias, String caption, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias, "VALUE", groupObject.objects, null, false, caption != null ? caption : (groupObject != null ? groupObject.getCaption() : "Объект"), groupObject.getValueClass().getUpSet(), commonFormOptions, propertyFormOptions, form);
    }
}
