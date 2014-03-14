package com.lsfusion.design.properties;

import com.intellij.designer.model.Property;
import com.intellij.designer.propertyTable.PropertyRenderer;
import com.intellij.designer.propertyTable.renderers.LabelPropertyRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReflectionProperty extends ReadOnlyProperty {
    private final PropertyRenderer renderer;

    public ReflectionProperty(@NotNull String propName) {
        this(propName, propName);
    }
    
    public ReflectionProperty(@NotNull String name, @NotNull String propName) {
        this(null, name, propName, new LabelPropertyRenderer(null));
    }
    
    public ReflectionProperty(@Nullable Property parent, @NotNull String name, @NotNull String propName, PropertyRenderer renderer) {
        super(parent, name, new ReflectionGetter(propName));
        this.renderer = renderer;
    }

    @NotNull
    @Override
    public PropertyRenderer getRenderer() {
        return renderer;
    }

    public static final class ReflectionGetter implements Getter {
        private final String propName;

        public ReflectionGetter(String propName) {
            this.propName = propName;
        }

        @Override
        public Object get(Object container) {
            return ReflectionUtil.getObjectProperty(container, propName);
        }
    }
}
