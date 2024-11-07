package com.lsfusion.design.model.proxy;

import com.lsfusion.design.FormView;
import com.lsfusion.design.model.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class ViewProxyFactory {
    private final static class InstanceHolder {
        private final static ViewProxyFactory instance = new ViewProxyFactory();
    }

    public static ViewProxyFactory getInstance() {
        return InstanceHolder.instance;
    }

    public static final Map<Class<?>, Class<? extends ViewProxy>> PROXY_CLASSES = new LinkedHashMap(){
        {
            put(ComponentView.class, ComponentViewProxy.class);
            put(ClassChooserView.class, ClassChooserViewProxy.class);
            put(ContainerView.class, ContainerViewProxy.class);
            put(FormView.class, FormViewProxy.class);
            put(GridView.class, GridViewProxy.class);
            put(PropertyDrawView.class, PropertyDrawViewProxy.class);
            put(ToolbarView.class, ToolbarViewProxy.class);
            put(FilterView.class, FilterViewProxy.class);
            put(TreeGroupView.class, TreeGroupProxy.class);
        }
    };

    public ViewProxy createViewProxy(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("Object can't be null");
        }

        Class cz = target.getClass();
        while (cz != null && !PROXY_CLASSES.containsKey(cz)) {
            cz = cz.getSuperclass();
        }

        if (cz == null) {
            throw new RuntimeException("View proxy isn't supported for the object!");
        }

        Class<? extends ViewProxy> proxyClass = PROXY_CLASSES.get(cz);
        try {
            return proxyClass.getConstructor(cz).newInstance(target);
        } catch (Exception e) {
            throw new RuntimeException("Can't create object: ", e);
        }
    }
}
