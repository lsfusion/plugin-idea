package com.lsfusion.design.properties;

import com.lsfusion.design.model.FontInfo;
import com.lsfusion.design.properties.converters.FontInfoConverter;
import com.lsfusion.design.properties.converters.KeyStrokeConverter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

import javax.swing.*;

public class ReflectionUtil {
    static {
        ConvertUtils.register(new FontInfoConverter(), FontInfo.class);
        ConvertUtils.register(new KeyStrokeConverter(), KeyStroke.class);
    }

    /**
     * not thread-safe
     */
    public static void setObjectProperty(Object propertyReceiver, String propertyName, Object propertyValue) {
        if (propertyReceiver == null) {
            throw new RuntimeException("object is undefined");
        }

        if (!PropertyUtils.isWriteable(propertyReceiver, propertyName)) {
            throw new RuntimeException("property doesn't exist");
        }

        try {
            BeanUtils.setProperty(propertyReceiver, propertyName, propertyValue);
        } catch (Exception e) {
            throw new RuntimeException("property can't be set: " + e.getMessage());
        }
    }

    public static Object getObjectProperty(Object propertySource, String propertyName) {
        if (propertySource == null) {
            throw new RuntimeException("object is undefined");
        }

        if (!PropertyUtils.isReadable(propertySource, propertyName)) {
            throw new RuntimeException("property doesn't exist");
        }

        try {
            return BeanUtils.getProperty(propertySource, propertyName);
        } catch (Exception e) {
            throw new RuntimeException("property can't be set: " + e.getMessage());
        }
    }
}
