package com.lsfusion.lang.folding;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

// "Collapse by default" choices of Settings > Editor > General > Code Folding > lsFusion, read by LSFFoldingBuilder.
// The inferred property classes fold ("Property Classes" in the LSF menu) is a per-project display mode and stays
// in PropertyFoldingManager.
@Service(Service.Level.APP)
@State(name = "LSFCodeFoldingSettings", storages = @Storage("editor.xml"))
public final class LSFCodeFoldingSettings implements PersistentStateComponent<LSFCodeFoldingSettings> {
    public boolean COLLAPSE_META_BODIES = false;
    public boolean COLLAPSE_ACTION_BODIES = false;
    public boolean COLLAPSE_FORM_BODIES = false;
    public boolean COLLAPSE_DESIGN_COMPONENTS = false;
    public boolean COLLAPSE_NAVIGATOR = false;

    public static LSFCodeFoldingSettings getInstance() {
        return ApplicationManager.getApplication().getService(LSFCodeFoldingSettings.class);
    }

    @Override
    public LSFCodeFoldingSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LSFCodeFoldingSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
