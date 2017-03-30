package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;

public class AddObjectActionProperty extends PropertyDrawEntity {
    public AddObjectActionProperty(String alias, GroupObjectEntity groupObject, LSFPropDeclaration propDeclaration, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias, "NEW", groupObject.objects, propDeclaration, commonFormOptions, propertyFormOptions, form);
    }

    @Override
    protected void initDefaultView() {
        super.initDefaultView();

        caption = "Добавить";
        isAction = true;
        iconPath = "add.png";
        forceViewType = ClassViewType.TOOLBAR;
        editKey = KeyStrokes.getAddActionPropertyKeyStroke();
        showEditKey = false;
    }
}
