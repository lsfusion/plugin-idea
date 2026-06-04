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

/**
 * Base inspection that reports usages of lsFusion constructs deprecated since a single platform version, so deprecations
 * can be disabled / re-leveled / suppressed per version via Settings | Editor | Inspections.
 */
public abstract class LSFDeprecatedVersionInspection extends LocalInspectionTool {

    @NotNull
    protected abstract String getVersion();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new LSFElementVisitor() {
            @Override
            public void visit(PsiElement element) {
                LSFProblemsVisitor.visitDeprecations(element, (e, version, text, fix) -> {
                    if (getVersion().equals(version)) {
                        holder.registerProblem(e, text, ProblemHighlightType.LIKE_DEPRECATED,
                                fix == null ? LocalQuickFix.EMPTY_ARRAY : new LocalQuickFix[]{fix});
                    }
                });
            }
        };
    }

    @Nullable
    @Override
    public String getStaticDescription() {
        return "Reports usages of lsFusion constructs deprecated since platform version " + getVersion() + ".";
    }
}
