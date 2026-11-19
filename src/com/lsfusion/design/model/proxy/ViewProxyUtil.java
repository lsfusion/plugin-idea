package com.lsfusion.design.model.proxy;

import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.design.model.FontInfo;
import com.lsfusion.design.properties.ReflectionUtil;
import com.lsfusion.design.properties.converters.FontInfoConverter;
import org.apache.commons.lang3.ClassUtils;

import javax.swing.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

public class ViewProxyUtil {
    private static final Map<Object, ViewProxy> viewProxies = ContainerUtil.createSoftMap();

    private static final Object NOT_CONVERTED = new Object();

    /**
     * not thread-safe
     */
    public static void setObjectProperty(Object propertyReceiver, String propertyName, Object propertyValue) {
        if (propertyReceiver == null) {
            throw new RuntimeException("object is undefined");
        }

        ViewProxy viewProxy;
        try {
            viewProxy = getViewProxy(propertyReceiver);
        } catch (Exception e) {
            return;
        }

        PropertyDescriptor descriptor = ReflectionUtil.getPropertyDescriptor(viewProxy.getClass(), propertyName);
        Method setter = descriptor != null ? descriptor.getWriteMethod() : null;
        if (setter == null) {
            return;
        }

        Object value = convert(propertyValue, setter.getParameterTypes()[0]);
        if (value == NOT_CONVERTED) {
            return;
        }

        try {
            setter.invoke(viewProxy, value);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    // A DESIGN value is a Boolean, a Dimension, an enum constant, or the text of an expression ("12", "'Tahoma 12'", ...),
    // which the preview can only read as a literal; it is null where the preview reads nothing. A value that does not fit
    // the setter leaves the property as it is.
    private static Object convert(Object value, Class<?> type) {
        if (value == null) {
            return NOT_CONVERTED;
        }

        Class<?> boxedType = ClassUtils.primitiveToWrapper(type);
        if (boxedType.isInstance(value)) {
            return value;
        }
        if (boxedType == String.class) {
            return value.toString();
        }

        String text = value.toString().trim();
        try {
            if (boxedType == Integer.class) {
                return Integer.valueOf(text);
            }
            if (boxedType == Long.class) {
                return Long.valueOf(text);
            }
            if (boxedType == Double.class) {
                return Double.valueOf(text);
            }
            if (boxedType == Boolean.class && (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("false"))) {
                return Boolean.valueOf(text);
            }
            if (boxedType == FontInfo.class) {
                return FontInfoConverter.convert(text);
            }
            if (boxedType == KeyStroke.class) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(text);
                if (keyStroke != null) {
                    return keyStroke;
                }
            }
        } catch (RuntimeException ignored) { // a text it cannot parse: a number, an unmatched quote in a font name, ...
        }
        return NOT_CONVERTED;
    }

    public static ViewProxy getViewProxy(Object target) {
        if (target == null) {
            return null;
        }

        ViewProxy proxy = viewProxies.get(target);
        if (proxy == null) {
            proxy = ViewProxyFactory.getInstance().createViewProxy(target);
            viewProxies.put(target, proxy);
        }

        return proxy;
    }
}
