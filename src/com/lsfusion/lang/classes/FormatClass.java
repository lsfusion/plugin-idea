package com.lsfusion.lang.classes;

import com.lsfusion.design.model.PropertyDrawView;

import java.text.Format;

public abstract class FormatClass extends DataClass {

    public abstract Format getDefaultFormat();

    public abstract Format createUserFormat(String pattern);


    protected Format getEditFormat(PropertyDrawView propertyDrawView) {
        return getEditFormat(propertyDrawView.getFormat());
    }
    protected Format getEditFormat(Format format) {
        return format;
    }
    
    @Override
    protected String getDefaultWidthString(PropertyDrawView propertyDraw) {
        Object defaultWidthValue = getDefaultWidthValue();
        if(defaultWidthValue != null)
            return getEditFormat(propertyDraw).format(defaultWidthValue);
        return super.getDefaultWidthString(propertyDraw);
    }

    protected Object getDefaultWidthValue() {
        return null;
    }
}
