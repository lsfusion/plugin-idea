package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;
import com.lsfusion.util.BaseUtils;

public class DeleteAction extends PropertyDrawEntity {
    public DeleteAction(String alias, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form, boolean oldSession) {
        super(alias != null ? alias : "delete" + (oldSession ? "Session" : "") + "_" + BaseUtils.capitalize(groupObject.getValueClass().getName()),
                null, commonFormOptions, propertyFormOptions, form);

        caption = "Удалить";
        isAction = true;
        iconPath = "delete.png";
        editKey = KeyStrokes.getDeleteActionPropertyKeyStroke();
        showEditKey = false;
    }
}