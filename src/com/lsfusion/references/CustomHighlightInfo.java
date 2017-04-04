package com.lsfusion.references;

import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.annotation.ProblemGroup;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomHighlightInfo extends HighlightInfo {
    private static final String ANNOTATOR_INSPECTION_SHORT_NAME = "Annotator";

    protected CustomHighlightInfo(@Nullable TextAttributes forcedTextAttributes, @Nullable TextAttributesKey forcedTextAttributesKey, @NotNull HighlightInfoType type, int startOffset, int endOffset, @Nullable String escapedDescription, @Nullable String escapedToolTip, @NotNull HighlightSeverity severity, boolean afterEndOfLine, @Nullable Boolean needsUpdateOnTyping, boolean isFileLevelAnnotation, int navigationShift, ProblemGroup problemGroup, GutterMark gutterIconRenderer) {
        super(forcedTextAttributes, forcedTextAttributesKey, type, startOffset, endOffset, escapedDescription, escapedToolTip, severity, afterEndOfLine, needsUpdateOnTyping, isFileLevelAnnotation, navigationShift, problemGroup, gutterIconRenderer);
    }

    @NotNull
    public static HighlightInfo fromAnnotation(@NotNull Annotation annotation, @Nullable TextRange fixedRange, boolean batchMode) {
        final TextAttributes forcedAttributes = annotation.getEnforcedTextAttributes();
        TextAttributesKey key = annotation.getTextAttributes();
        final TextAttributesKey forcedAttributesKey = forcedAttributes == null ? (key == HighlighterColors.NO_HIGHLIGHTING ? null : key) : null;

        HighlightInfo info = new CustomHighlightInfo(forcedAttributes, forcedAttributesKey, convertType(annotation),
                fixedRange != null? fixedRange.getStartOffset() : annotation.getStartOffset(),
                fixedRange != null? fixedRange.getEndOffset() : annotation.getEndOffset(),
                annotation.getMessage(), annotation.getTooltip(),
                annotation.getSeverity(), annotation.isAfterEndOfLine(), annotation.needsUpdateOnTyping(), annotation.isFileLevelAnnotation(),
                0, annotation.getProblemGroup(), annotation.getGutterIconRenderer());
        appendFixes(fixedRange, info, batchMode ? annotation.getBatchFixes() : annotation.getQuickFixes());
        return info;
    }

    @NotNull
    private static HighlightInfoType convertType(@NotNull Annotation annotation) {
        ProblemHighlightType type = annotation.getHighlightType();
        if (type == ProblemHighlightType.LIKE_UNUSED_SYMBOL) return HighlightInfoType.UNUSED_SYMBOL;
        if (type == ProblemHighlightType.LIKE_UNKNOWN_SYMBOL) return HighlightInfoType.WRONG_REF;
        if (type == ProblemHighlightType.LIKE_DEPRECATED) return HighlightInfoType.DEPRECATED;
        return convertSeverity(annotation.getSeverity());
    }

    private static void appendFixes(@Nullable TextRange fixedRange, @NotNull HighlightInfo info, @Nullable List<Annotation.QuickFixInfo> fixes) {
        if (fixes != null) {
            for (final Annotation.QuickFixInfo quickFixInfo : fixes) {
                TextRange range = fixedRange != null ? fixedRange : quickFixInfo.textRange;
                HighlightDisplayKey key = quickFixInfo.key != null
                        ? quickFixInfo.key
                        : HighlightDisplayKey.find(ANNOTATOR_INSPECTION_SHORT_NAME);
                info.registerFix(quickFixInfo.quickFix, null, HighlightDisplayKey.getDisplayNameByKey(key), range, key);
            }
        }
    }
}
