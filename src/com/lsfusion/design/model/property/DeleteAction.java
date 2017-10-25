package com.lsfusion.design.model.property;

import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;

public class DeleteAction extends PropertyDrawEntity {
    public DeleteAction(String alias, GroupObjectEntity groupObject, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        super(alias, "DELETE", groupObject.objects,
                null, commonFormOptions, propertyFormOptions, form);
    }

    @Override
    protected void initDefaultView() {
        super.initDefaultView();

        caption = "Удалить";
        isAction = true;
        iconPath = "delete.png";
        changeKey = KeyStrokes.getDeleteActionPropertyKeyStroke();
        showChangeKey = false;
        forceViewType = ClassViewType.TOOLBAR;
    }
}
