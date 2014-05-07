package com.lsfusion.design.model.property;

import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;

public class ObjectValueProperty extends PropertyDrawEntity {
    public ObjectValueProperty(String alias, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias != null ? alias : "objectValueProperty_" + groupObject.objects.get(0).sID, null, commonFormOptions, propertyFormOptions, form);
        caption = groupObject != null ? groupObject.getCaption() : "Объект";
    }
}