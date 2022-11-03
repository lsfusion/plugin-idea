package com.lsfusion.formatter;

import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.lsfusion.lang.LSFLanguage;
import org.jetbrains.annotations.NotNull;

public class LSFLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    @NotNull
    @Override
    public Language getLanguage() {
        return LSFLanguage.INSTANCE;
    }

    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new CustomCodeStyleSettings("LSF", settings) {};
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return CodeStyleAbstractPanel.readFromFile(this.getClass(), "preview.html.indent.template");
    }
}