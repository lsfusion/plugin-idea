package com.lsfusion.design.properties;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class ReflectionUtil {
    // a JavaBeans property, as Introspector sees it: a getter and a setter of different types are not one property
    public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
        try {
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(beanClass).getPropertyDescriptors())
                if (descriptor.getName().equals(propertyName))
                    return descriptor;
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // the property table shows a value as its text
    public static Object getObjectProperty(Object propertySource, String propertyName) {
        if (propertySource == null) {
            throw new RuntimeException("object is undefined");
        }

        PropertyDescriptor descriptor = getPropertyDescriptor(propertySource.getClass(), propertyName);
        if (descriptor == null || descriptor.getReadMethod() == null) {
            throw new RuntimeException("property doesn't exist");
        }

        try {
            Object value = descriptor.getReadMethod().invoke(propertySource);
            return value != null ? value.toString() : null;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("property can't be read: " + e.getMessage());
        }
    }
}
