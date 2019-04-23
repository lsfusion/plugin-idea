package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;

public class EditFormAction extends PropertyDrawEntity {
    public EditFormAction(String alias, String caption, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias, "EDIT", caption != null ? caption : "Edit", groupObject.objects, commonFormOptions, propertyFormOptions, form);
    }

    @Override
    protected void initDefaultView() {
        super.initDefaultView();

        iconPath = "edit.png";
        changeKey = KeyStrokes.getEditActionPropertyKeyStroke();
        showChangeKey = false;
        forceViewType = ClassViewType.TOOLBAR;
    }
}
