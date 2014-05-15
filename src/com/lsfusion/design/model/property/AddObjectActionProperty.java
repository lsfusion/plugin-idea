package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.util.BaseUtils;

public class AddObjectActionProperty extends PropertyDrawEntity {
    public AddObjectActionProperty(String alias, GroupObjectEntity groupObject, LSFPropDeclaration propDeclaration, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias != null ? alias :
                "addObject" + "_" + BaseUtils.capitalize(groupObject != null ? groupObject.getValueClass().getName() : "valueClass") +
                        "_" + BaseUtils.capitalize(form.sID) +
                        "_" + BaseUtils.capitalize(groupObject != null ? groupObject.objects.get(0).sID : "class"), propDeclaration, commonFormOptions, propertyFormOptions, form);
        caption = "Добавить";
        isAction = true;
        iconPath = "add.png";
        toolbar = true;
        forceViewType = ClassViewType.PANEL;
        editKey = KeyStrokes.getAddActionPropertyKeyStroke();
        showEditKey = false;
    }
}
