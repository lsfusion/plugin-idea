package com.lsfusion.inspections;

import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reports declarations and usages of lsFusion constructs marked with the {@code @@deprecated} annotation.
 * Unlike the per-version inspections it highlights them with the warning color (like Java's "Deprecated API usage")
 * instead of strikethrough; the {@code @@deprecated} marker itself is additionally colored by the annotator.
 */
public class LSFDeprecatedAnnotationInspection extends LSFDeprecatedVersionInspection {
    @Nullable
    @Override
    protected String getVersion() {
        return null;
    }

    @NotNull
    @Override
    protected ProblemHighlightType getHighlightType() {
        return ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
    }
}
