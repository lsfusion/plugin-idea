package com.lsfusion.design.properties;

import com.intellij.designer.model.PropertiesContainer;
import com.intellij.designer.model.PropertyContext;
import com.intellij.designer.propertyTable.PropertyRenderer;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Minimal read-only text renderer for property values, used by {@link ReflectionProperty}.
 * Replaces the platform-internal {@code com.intellij.designer.propertyTable.renderers.LabelPropertyRenderer}.
 */
public class TextPropertyRenderer extends JBLabel implements PropertyRenderer {
    @NotNull
    @Override
    public JComponent getComponent(@Nullable PropertiesContainer container,
                                   @Nullable PropertyContext context,
                                   @Nullable Object value,
                                   boolean selected,
                                   boolean hasFocus) {
        setText(value == null ? "" : value.toString());
        return this;
    }
}
