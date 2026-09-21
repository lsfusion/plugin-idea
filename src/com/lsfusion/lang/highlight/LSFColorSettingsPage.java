package com.lsfusion.lang.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

import static com.lsfusion.lang.highlight.LSFHighlightingColors.*;

// Settings > Editor > Color Scheme > lsFusion
public class LSFColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = {
            new AttributesDescriptor("Syntax//Keyword", KEYWORD),
            new AttributesDescriptor("Syntax//String", STRING_LITERAL),
            new AttributesDescriptor("Syntax//Raw string", RAW_STRING_LITERAL),
            new AttributesDescriptor("Syntax//Number, date, color literal", NUMBER_LITERAL),
            new AttributesDescriptor("Syntax//Comment", COMMENT),
            new AttributesDescriptor("Syntax//Bad character", BAD_CHARACTER),

            new AttributesDescriptor("META//Declaration body", META_DECL),
            new AttributesDescriptor("META//Usage (inlined code)", META_USAGE),
            new AttributesDescriptor("META//Usage inside a declaration", META_DECL_USAGE),
            new AttributesDescriptor("META//Nested usage", META_NESTING_USAGE),
            new AttributesDescriptor("META//Error in a declaration", META_ERROR),
            new AttributesDescriptor("META//Error in a declaration (underlined)", META_ERROR_UNDERSCORED),

            new AttributesDescriptor("Parameters//Implicit parameter declaration", IMPLICIT_DECL),
            new AttributesDescriptor("Parameters//Untyped implicit parameter declaration", UNTYPED_IMPLICIT_DECL),
            new AttributesDescriptor("Parameters//Outer parameter", OUTER_PARAM),

            new AttributesDescriptor("Problems//Error", ERROR),
            new AttributesDescriptor("Problems//Error (underlined)", ERROR_UNDERSCORED),
            new AttributesDescriptor("Problems//Warning (underlined)", WARNING_UNDERSCORED),
            new AttributesDescriptor("Problems//Deprecated annotation", DEPRECATED_ANNOTATION),
    };

    // the annotator does not run on the demo, so its colors are put in by tags
    private static final Map<String, TextAttributesKey> TAGS = Map.ofEntries(
            Map.entry("meta_decl", META_DECL),
            Map.entry("meta_usage", META_USAGE),
            Map.entry("meta_decl_usage", META_DECL_USAGE),
            Map.entry("meta_nesting", META_NESTING_USAGE),
            Map.entry("meta_error", META_ERROR),
            Map.entry("meta_error_underlined", META_ERROR_UNDERSCORED),
            Map.entry("implicit", IMPLICIT_DECL),
            Map.entry("untyped", UNTYPED_IMPLICIT_DECL),
            Map.entry("outer", OUTER_PARAM),
            Map.entry("error", ERROR),
            Map.entry("error_underlined", ERROR_UNDERSCORED),
            Map.entry("warning", WARNING_UNDERSCORED),
            Map.entry("deprecated", DEPRECATED_ANNOTATION)
    );

    @Override
    public @Nullable Icon getIcon() {
        return LSFIcons.FILE;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new LSFSyntaxHighlighter();
    }

    @Override
    public @NotNull String getDemoText() {
        return "MODULE Sample;\n" +
               "\n" +
               "REQUIRE System, Time;\n" +
               "\n" +
               "// Items and their prices\n" +
               "CLASS Item 'Item';\n" +
               "name 'Name' = DATA STRING[100] (Item);\n" +
               "price 'Price' = DATA NUMERIC[10,2] (Item);\n" +
               "created 'Created' = DATA DATE (Item);\n" +
               "\n" +
               "<deprecated>@deprecated</deprecated>\n" +
               "oldName 'Name' (Item i) = name(i);\n" +
               "\n" +
               "sum 'Sum' (<implicit>Item i</implicit>) = price(i) * 2;\n" +
               "total 'Total' = GROUP SUM price(<untyped>Item i</untyped>) IF created(i) > 2024_01_01;\n" +
               "description 'Description' (Item i) = <error>unknownProperty</error>(i) + <error_underlined>badName</error_underlined>(i);\n" +
               "\n" +
               "message 'Message' = 'Created ' + <warning>sampleBundleKey</warning>;\n" +
               "\n" +
               "<meta_decl>META itemForm(name, caption)\n" +
               "    FORM name caption\n" +
               "        OBJECTS i = Item\n" +
               "        PROPERTIES(i) <meta_error>unknown</meta_error>, <meta_error_underlined>missing</meta_error_underlined>, price\n" +
               "    ;\n" +
               "    <meta_decl_usage>@editItem(name);</meta_decl_usage>\n" +
               "END</meta_decl>\n" +
               "\n" +
               "<meta_usage>@itemForm(items, 'Items');</meta_usage>\n" +
               "<meta_nesting>@itemForm(allItems, 'All items');</meta_nesting>\n" +
               "\n" +
               "createItem 'Create item' (STRING[100] name) {\n" +
               "    NEW i = Item {\n" +
               "        name(i) <- name;\n" +
               "        FOR price(Item o) > price(i) DO\n" +
               "            price(<outer>i</outer>) <- price(o);\n" +
               "    }\n" +
               "    APPLY;\n" +
               "}\n";
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return TAGS;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull String getDisplayName() {
        return LSFLanguage.INSTANCE.getDisplayName();
    }
}
