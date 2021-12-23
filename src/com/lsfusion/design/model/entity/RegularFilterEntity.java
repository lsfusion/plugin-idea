package com.lsfusion.design.model.entity;

import com.lsfusion.design.KeyStrokes;

import javax.swing.*;
import java.util.Set;

public class RegularFilterEntity {
    public String name = "";
    public KeyStroke key;
    public boolean showKey = true;
    public Set<ObjectEntity> objects;
    public boolean isDefault;

    public RegularFilterEntity(String iname, KeyStroke ikey, Set<ObjectEntity> objects, boolean isDefault) {
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
