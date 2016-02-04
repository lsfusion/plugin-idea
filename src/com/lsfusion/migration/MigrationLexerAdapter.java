package com.lsfusion.migration;

import com.intellij.lexer.FlexAdapter;

public class MigrationLexerAdapter extends FlexAdapter {
    public MigrationLexerAdapter() {
        super(new MigrationLexer());
    }
}
