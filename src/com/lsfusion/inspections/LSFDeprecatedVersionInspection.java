package com.lsfusion.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Base inspection that reports usages of lsFusion constructs deprecated since a single platform version, so deprecations
 * can be disabled / re-leveled / suppressed per version via Settings | Editor | Inspections.
 */
public abstract class LSFDeprecatedVersionInspection extends LocalInspectionTool {

    /** Platform version this inspection reports (e.g. {@code "5.2"}); {@code null} = unversioned (@@deprecated). */
    @Nullable
    protected abstract String getVersion();

    /** How matched deprecations are highlighted; defaults to strikethrough ("Deprecated symbol"). */
    @NotNull
    protected ProblemHighlightType getHighlightType() {
        return ProblemHighlightType.LIKE_DEPRECATED;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new LSFElementVisitor() {
            @Override
            public void visit(PsiElement element) {
                LSFProblemsVisitor.visitDeprecations(element, (e, version, text, fix) -> {
                    if (Objects.equals(getVersion(), version)) {
                        holder.registerProblem(e, text, getHighlightType(),
                                fix == null ? LocalQuickFix.EMPTY_ARRAY : new LocalQuickFix[]{fix});
                    }
                });
            }
        };
    }

    @Nullable
    @Override
    public String getStaticDescription() {
        String version = getVersion();
        return version == null
                ? "Reports declarations and usages of lsFusion constructs marked with the @@deprecated annotation."
                : "Reports usages of lsFusion constructs deprecated since platform version " + version + ".";
    }
}
