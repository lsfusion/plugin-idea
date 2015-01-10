package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.FormSessionScope;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;

public class AddFormAction extends PropertyDrawEntity {
    public AddFormAction(String alias, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form, FormSessionScope scope) {
        super(alias, "ADDFORM", groupObject.objects, null, commonFormOptions, propertyFormOptions, form);
        caption = "Добавить";
        isAction = true;
        iconPath = "add.png";
        editKey = KeyStrokes.getAddActionPropertyKeyStroke();
        showEditKey = false;
        toolbar = true;
        forceViewType = ClassViewType.PANEL;
    }
}
