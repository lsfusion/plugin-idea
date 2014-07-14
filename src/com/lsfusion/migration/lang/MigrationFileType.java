package com.lsfusion.migration.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.lsfusion.MigrationIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MigrationFileType extends LanguageFileType {
    public static final MigrationFileType INSTANCE = new MigrationFileType();

    private MigrationFileType() {
        super(MigrationLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "lsFusion migration script file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "lsFusion migration script file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "script";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MigrationIcons.FILE;
    }
}