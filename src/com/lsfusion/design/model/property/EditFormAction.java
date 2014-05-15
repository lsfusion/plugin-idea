package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;
import com.lsfusion.util.BaseUtils;

public class EditFormAction extends PropertyDrawEntity {
    public EditFormAction(String alias, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form, boolean oldSession) {
        super(alias != null ? alias : "editForm" + (oldSession ? "Session" : "") + "_" + BaseUtils.capitalize(groupObject != null ? groupObject.getValueClass().getName() : "valueClass"),
                null, commonFormOptions, propertyFormOptions, form);

        caption = "Редактировать";
        isAction = true;
        iconPath = "edit.png";
        editKey = KeyStrokes.getEditActionPropertyKeyStroke();
        showEditKey = false;
        toolbar = true;
        forceViewType = ClassViewType.PANEL;
    }
}
