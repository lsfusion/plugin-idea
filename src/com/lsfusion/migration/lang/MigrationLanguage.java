package com.lsfusion.migration.lang;

import com.intellij.lang.Language;

public class MigrationLanguage extends Language {
    public static final MigrationLanguage INSTANCE = new MigrationLanguage();
    
    private MigrationLanguage() {
        super("LsfMigration");
    }
}
