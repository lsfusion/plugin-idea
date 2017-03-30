package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.classes.LogicalClass;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;

public class SelectionProperty extends PropertyDrawEntity {
    public SelectionProperty(String alias, GroupObjectEntity groupObject, String sIDPostfix, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias, "SELECTION", groupObject.objects, null, commonFormOptions, propertyFormOptions, form);
   }

    @Override
    protected void initDefaultView() {
        super.initDefaultView();

        caption = "Отметить";
        baseClass = new LogicalClass();
    }
}
