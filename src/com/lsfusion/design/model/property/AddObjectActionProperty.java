package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;

public class AddObjectActionProperty extends PropertyDrawEntity {
    public AddObjectActionProperty(String alias, String caption, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias, "NEW", caption != null ? caption : "Добавить", groupObject.objects, commonFormOptions, propertyFormOptions, form);
    }

    @Override
    protected void initDefaultView() {
        super.initDefaultView();

        iconPath = "add.png";
        forceViewType = ClassViewType.TOOLBAR;
        changeKey = KeyStrokes.getAddActionPropertyKeyStroke();
        showChangeKey = false;
    }
}
