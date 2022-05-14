package com.lsfusion.module;

import com.intellij.openapi.module.ModuleType;
import com.lsfusion.LSFBundle;
import com.lsfusion.LSFIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LSFModuleType extends ModuleType<LSFusionModuleBuilder> {
    public static final LSFModuleType INSTANCE = new LSFModuleType("lsfusion");
    
    protected LSFModuleType(@NotNull @NonNls String id) {
        super(id);
    }

    @NotNull
    @Override
    public LSFusionModuleBuilder createModuleBuilder() {
        return new LSFusionModuleBuilder();
    }

    @Override
    public @NotNull
    @Nls(capitalization = Nls.Capitalization.Title) String getName() {
        return LSFBundle.message("lsf.module.type.name");
    }

    @Override
    public @NotNull
    @Nls(capitalization = Nls.Capitalization.Sentence) String getDescription() {
        return LSFBundle.message("lsf.module.type.description");
    }

    @Override
    public @NotNull Icon getNodeIcon(boolean isOpened) {
        return LSFIcons.MODULE;
    }
}
