package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;

public class ObjectValueProperty extends PropertyDrawEntity {
    public ObjectValueProperty(String alias, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias, "OBJVALUE", groupObject.objects, null, commonFormOptions, propertyFormOptions, form);
        if(caption.equals("Объект") && groupObject != null)
            caption = groupObject.getCaption();
    }

    @Override
    protected void initDefaultView() {
        super.initDefaultView();

        caption = "Объект";
    }
}
