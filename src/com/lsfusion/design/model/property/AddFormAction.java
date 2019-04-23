package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;

public class AddFormAction extends PropertyDrawEntity {
    public AddFormAction(String alias, String caption, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias, "NEWEDIT", caption != null ? caption : "Add", groupObject.objects, commonFormOptions, propertyFormOptions, form);
    }

    @Override
    protected void initDefaultView() {
        super.initDefaultView();

        iconPath = "add.png";
        changeKey = KeyStrokes.getAddActionPropertyKeyStroke();
        showChangeKey = false;
        forceViewType = ClassViewType.TOOLBAR;
    }
}
