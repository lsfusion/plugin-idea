package com.lsfusion.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.lsfusion.lang.psi.LSFSimpleNameWithCaption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFUnusedInspectionTool extends LocalInspectionTool {



    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new LSFElementVisitor() {

            @Override
            public void visit(PsiElement element) {
                if (element instanceof LSFSimpleNameWithCaption) {
                    LSFProblemsVisitor.visitLSFSimpleNameWithCaption(holder, (LSFSimpleNameWithCaption) element, false);
                }
            }

            @Override
            protected boolean disabledInMeta() {
                return true;
            }
        };
    }

    @Nullable
    @Override
    public String getStaticDescription() {
        return "Reports unused properties, classes, groups.";
    }
}