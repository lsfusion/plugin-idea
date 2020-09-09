package com.lsfusion.design.model.proxy;

import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.design.model.FontInfo;
import com.lsfusion.design.properties.converters.FontInfoConverter;
import com.lsfusion.design.properties.converters.KeyStrokeConverter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

import javax.swing.*;
import java.util.Map;

public class ViewProxyUtil {
    static {
        ConvertUtils.register(new FontInfoConverter(), FontInfo.class);
        ConvertUtils.register(new KeyStrokeConverter(), KeyStroke.class);
    }

    private static final Map<Object, ViewProxy> viewProxies = ContainerUtil.createSoftMap();

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

        if (!PropertyUtils.isWriteable(viewProxy, propertyName)) {
            return;
        }

        try {
            BeanUtils.setProperty(viewProxy, propertyName, propertyValue);
        } catch (Exception e) {
            return;
        }
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
