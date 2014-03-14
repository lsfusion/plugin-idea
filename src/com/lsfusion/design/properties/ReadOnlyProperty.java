package com.lsfusion.design.properties;

import com.intellij.designer.model.PropertiesContainer;
import com.intellij.designer.model.Property;
import com.intellij.designer.propertyTable.PropertyEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ReadOnlyProperty<T extends PropertiesContainer> extends Property<T> {
    public interface Getter<T> {
        Object get(T container);
    }
    
    private final Getter getter;
    
    public ReadOnlyProperty(@Nullable Property parent, @NotNull String name, Getter getter) {
        super(parent, name);
        this.getter = getter;
    }

    @Nullable
    @Override
    public Object getValue(@NotNull T container) throws Exception {
        return getter.get(container);
    }

    @Override
    public final void setValue(@NotNull T container, @Nullable Object value) throws Exception {
        throw new IllegalStateException("shouldn't be called");
    }

    @Nullable
    @Override
    public final PropertyEditor getEditor() {
        return null;
    }
    
    public ReadOnlyProperty setExpert() {
        setExpert(true);
        return this;
    }
    
    public ReadOnlyProperty setImportant() {
        setImportant(true);
        return this;
    }
}
