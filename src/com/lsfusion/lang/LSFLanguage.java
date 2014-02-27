
package com.lsfusion.lang;

import com.intellij.lang.Language;

public class LSFLanguage extends Language {
    public static final LSFLanguage INSTANCE = new LSFLanguage();

    private LSFLanguage() {
        super("Lsf");
    }
}