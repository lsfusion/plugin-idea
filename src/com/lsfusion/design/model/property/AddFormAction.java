package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.FormSessionScope;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;
import com.lsfusion.util.BaseUtils;

public class AddFormAction extends PropertyDrawEntity {
    public AddFormAction(String alias, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form, FormSessionScope scope) {
        super(alias(alias, "addForm", scope, groupObject), null, commonFormOptions, propertyFormOptions, form);
        caption = "Добавить";
        isAction = true;
        iconPath = "add.png";
        editKey = KeyStrokes.getAddActionPropertyKeyStroke();
        showEditKey = false;
        toolbar = true;
        forceViewType = ClassViewType.PANEL;
    }

    public static String alias(String alias, String baseName, FormSessionScope scope, GroupObjectEntity groupObject) {
        if (alias != null) {
            return alias;
        }
        String name = baseName;
        switch (scope) {
            case OLDSESSION:
                name += "Session";
                break;
            case NESTEDSESSION:
                name += "Nested";
                break;
        }
        return name + "_" + BaseUtils.capitalize(groupObject != null ? groupObject.getValueClass().getName() : "valueClass");
    }
}
