package com.lsfusion.lang.folding;

import com.intellij.application.options.editor.CodeFoldingOptionsProvider;
import com.intellij.openapi.options.BeanConfigurable;
import com.lsfusion.lang.LSFLanguage;

// the lsFusion group of Settings > Editor > General > Code Folding
public class LSFCodeFoldingOptionsProvider extends BeanConfigurable<LSFCodeFoldingSettings> implements CodeFoldingOptionsProvider {
    public LSFCodeFoldingOptionsProvider() {
        super(LSFCodeFoldingSettings.getInstance(), LSFLanguage.INSTANCE.getDisplayName());
        LSFCodeFoldingSettings settings = getInstance();
        checkBox("META declaration bodies", () -> settings.COLLAPSE_META_BODIES, value -> settings.COLLAPSE_META_BODIES = value);
        checkBox("Action bodies", () -> settings.COLLAPSE_ACTION_BODIES, value -> settings.COLLAPSE_ACTION_BODIES = value);
        checkBox("FORM bodies", () -> settings.COLLAPSE_FORM_BODIES, value -> settings.COLLAPSE_FORM_BODIES = value);
        checkBox("DESIGN component bodies", () -> settings.COLLAPSE_DESIGN_COMPONENTS, value -> settings.COLLAPSE_DESIGN_COMPONENTS = value);
        checkBox("NAVIGATOR element bodies", () -> settings.COLLAPSE_NAVIGATOR, value -> settings.COLLAPSE_NAVIGATOR = value);
    }
}
