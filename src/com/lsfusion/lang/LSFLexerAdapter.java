package com.lsfusion.lang;

import com.intellij.lexer.FlexAdapter;

public class LSFLexerAdapter extends FlexAdapter {
    public LSFLexerAdapter() {
        super(new LSFLexer());
    }
}
