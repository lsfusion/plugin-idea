package com.lsfusion.lang.highlight;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

// Every color the plugin paints in the editor. The values live in the color scheme (defaults for Default / Darcula in
// resources/colorSchemes, everything else falls back to the standard key), so users and schemes can change them on the
// lsFusion page of Settings > Editor > Color Scheme.
public final class LSFHighlightingColors {
    // syntax
    public static final TextAttributesKey KEYWORD = createTextAttributesKey("LSF_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING_LITERAL = createTextAttributesKey("LSF_STRING_LITERAL", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey RAW_STRING_LITERAL = createTextAttributesKey("LSF_RAW_STRING_LITERAL", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER_LITERAL = createTextAttributesKey("LSF_LITERAL", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("LSF_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("LSF_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    // META (backgrounds; META_DECL_USAGE is a META usage inside a META declaration)
    public static final TextAttributesKey META_DECL = createTextAttributesKey("LSF_META_DECL", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey META_USAGE = createTextAttributesKey("LSF_META_USAGE", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey META_DECL_USAGE = createTextAttributesKey("LSF_META_DECL_USAGE", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey META_NESTING_USAGE = createTextAttributesKey("LSF_META_NESTING_USAGE", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey META_ERROR = createTextAttributesKey("LSF_META_ERROR", CodeInsightColors.WARNINGS_ATTRIBUTES);
    public static final TextAttributesKey META_ERROR_UNDERSCORED = createTextAttributesKey("LSF_META_ERROR_UNDERSCORED", CodeInsightColors.WARNINGS_ATTRIBUTES);

    // parameters
    public static final TextAttributesKey IMPLICIT_DECL = createTextAttributesKey("LSF_IMPLICIT_DECL", DefaultLanguageHighlighterColors.PARAMETER);
    public static final TextAttributesKey UNTYPED_IMPLICIT_DECL = createTextAttributesKey("LSF_UNTYPED_IMPLICIT_DECL", DefaultLanguageHighlighterColors.PARAMETER);
    public static final TextAttributesKey OUTER_PARAM = createTextAttributesKey("LSF_OUTER_PARAM", DefaultLanguageHighlighterColors.PARAMETER);

    // problems: the annotator paints resolving errors as colored text or as a wave, depending on the error
    public static final TextAttributesKey ERROR = createTextAttributesKey("LSF_ERROR", CodeInsightColors.ERRORS_ATTRIBUTES);
    public static final TextAttributesKey ERROR_UNDERSCORED = createTextAttributesKey("LSF_ERROR_UNDERSCORED", CodeInsightColors.ERRORS_ATTRIBUTES);
    public static final TextAttributesKey WARNING_UNDERSCORED = createTextAttributesKey("LSF_WARNING_UNDERSCORED", CodeInsightColors.WEAK_WARNING_ATTRIBUTES);
    public static final TextAttributesKey DEPRECATED_ANNOTATION = createTextAttributesKey("LSF_DEPRECATED_ANNOTATION", DefaultLanguageHighlighterColors.METADATA);

    private LSFHighlightingColors() {
    }
}
