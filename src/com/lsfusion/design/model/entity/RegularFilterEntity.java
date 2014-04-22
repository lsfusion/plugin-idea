package com.lsfusion.design.model.entity;

import com.lsfusion.design.KeyStrokes;

import javax.swing.*;
import java.util.List;

public class RegularFilterEntity {
    public String name = "";
    public KeyStroke key;
    public boolean showKey = true;
    public List<ObjectEntity> objects;
    public boolean isDefault;

    public RegularFilterEntity(String iname, KeyStroke ikey, List<ObjectEntity> objects) {
        this(iname, ikey, objects, false);
    }

    public RegularFilterEntity(String iname, KeyStroke ikey, List<ObjectEntity> objects, boolean isDefault) {
        name = iname;
        key = ikey;
        this.objects = objects;
        this.isDefault = isDefault;
    }

    public String getFullCaption() {

        String fullCaption = name;
        if (showKey && key != null) {
            fullCaption += " (" + KeyStrokes.getKeyStrokeCaption(key) + ")";
        }
        return fullCaption;
    }
}
