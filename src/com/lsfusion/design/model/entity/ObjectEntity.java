package com.lsfusion.design.model.entity;

import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.util.BaseUtils;

public class ObjectEntity {
    public String sID;

    public String caption;

    public GroupObjectEntity groupTo;

    public LSFValueClass valueClass;


    public ObjectEntity(String sID, LSFValueClass valueClass) {
        this.sID = sID;
        this.valueClass = valueClass;
    }

    public String getCaption() {
        return !BaseUtils.isRedundantString(caption)
                ? caption
                : !BaseUtils.isRedundantString(valueClass.getCaption())
                ? valueClass.getCaption()
                : "Неопределённый объект";
    }
}
