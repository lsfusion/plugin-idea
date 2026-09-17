package com.lsfusion.formatter;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizableOptions;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
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
    public CustomCodeStyleSettings createCustomSettings(@NotNull CodeStyleSettings settings) {
        return new LSFCodeStyleSettings(settings);
    }

    @Override
    protected void customizeDefaults(@NotNull CommonCodeStyleSettings commonSettings, @NotNull CommonCodeStyleSettings.IndentOptions indentOptions) {
        // the formatter has always collapsed blank lines to one, keep that as the default
        commonSettings.KEEP_BLANK_LINES_IN_DECLARATIONS = 1;
        commonSettings.KEEP_BLANK_LINES_IN_CODE = 1;
    }

    // only the options LSFCodeStyle actually reads (plus the right margin ones the platform applies itself) are shown
    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        switch (settingsType) {
            case SPACING_SETTINGS:
                consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS", "SPACE_AROUND_EQUALITY_OPERATORS",
                        "SPACE_AROUND_RELATIONAL_OPERATORS", "SPACE_AROUND_ADDITIVE_OPERATORS", "SPACE_AROUND_MULTIPLICATIVE_OPERATORS",
                        "SPACE_AFTER_COMMA", "SPACE_BEFORE_COMMA");
                consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Assignment operators (=, <-, +=)");
                consumer.renameStandardOption("SPACE_AROUND_EQUALITY_OPERATORS", "Equality operators (==, !=, =)");
                consumer.renameStandardOption("SPACE_AROUND_RELATIONAL_OPERATORS", "Relational operators (<, >, <=, >=)");
                consumer.renameStandardOption("SPACE_AROUND_ADDITIVE_OPERATORS", "Additive operators (+, -)");
                consumer.renameStandardOption("SPACE_AROUND_MULTIPLICATIVE_OPERATORS", "Multiplicative operators (*, /)");
                break;
            case BLANK_LINES_SETTINGS:
                consumer.showStandardOptions("KEEP_BLANK_LINES_IN_DECLARATIONS", "KEEP_BLANK_LINES_IN_CODE");
                consumer.showCustomOption(LSFCodeStyleSettings.class, "BLANK_LINES_IN_MODULE_HEADER", "Between module header statements",
                        CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES);
                consumer.showCustomOption(LSFCodeStyleSettings.class, "BLANK_LINES_BEFORE_OBJECTS", "Before OBJECTS in a form",
                        CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES);
                break;
            case WRAPPING_AND_BRACES_SETTINGS:
                // KEEP_LINE_BREAKS is deliberately absent: the blocks separate statements by the existing line breaks only
                consumer.showStandardOptions("RIGHT_MARGIN", "WRAP_ON_TYPING", "WRAP_LONG_LINES");
                break;
        }
    }

    @NotNull
    @Override
    public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings baseSettings, @NotNull CodeStyleSettings modelSettings) {
        return new CodeStyleAbstractConfigurable(baseSettings, modelSettings, getConfigurableDisplayName()) {
            @NotNull
            @Override
            protected CodeStyleAbstractPanel createPanel(@NotNull CodeStyleSettings settings) {
                return new TabbedLanguageCodeStylePanel(LSFLanguage.INSTANCE, getCurrentSettings(), settings) {};
            }
        };
    }

    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor(this);
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return "MODULE Sample;\n" +
               "\n" +
               "REQUIRE System,\n" +
               "        Time;\n" +
               "\n" +
               "NAMESPACE Sample;\n" +
               "\n" +
               "CLASS Item 'Item';\n" +
               "name 'Name' = DATA STRING[100] (Item);\n" +
               "price 'Price' = DATA NUMERIC[10,2] (Item);\n" +
               "quantity 'Quantity' = DATA INTEGER (Item);\n" +
               "sum 'Sum' (Item i) = price(i) * quantity(i);\n" +
               "discount 'Discount' (Item i) = IF quantity(i) >= 10 THEN sum(i) / 10 ELSE 0;\n" +
               "total 'Total' (Item i) = sum(i) - discount(i);\n" +
               "inStock 'In stock' (Item i) = quantity(i) != 0;\n" +
               "\n" +
               "FORM items 'Items'\n" +
               "    OBJECTS i = Item\n" +
               "    PROPERTIES(i) name, price, quantity, sum, discount, total, inStock, NEW, DELETE\n" +
               "\n" +
               "    OBJECTS d = DATE\n" +
               "    PROPERTIES(d) VALUE\n" +
               ";\n" +
               "\n" +
               "createItem 'Create item' (STRING[100] name, NUMERIC[10,2] price) {\n" +
               "    NEW i = Item {\n" +
               "        name(i) <- name;\n" +
               "        price(i) <- price;\n" +
               "        IF NOT quantity(i) THEN\n" +
               "            quantity(i) <- 1;\n" +
               "    }\n" +
               "    APPLY;\n" +
               "}\n";
    }
}
