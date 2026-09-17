package com.lsfusion.formatter;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

// lsFusion-specific code style options; everything else the formatter honours comes from CommonCodeStyleSettings
public class LSFCodeStyleSettings extends CustomCodeStyleSettings {
    public int BLANK_LINES_IN_MODULE_HEADER = 1;
    public int BLANK_LINES_BEFORE_OBJECTS = 1;

    public LSFCodeStyleSettings(CodeStyleSettings settings) {
        super("LSF", settings);
    }
}
