package com.lsfusion.inspections;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.LSFSimpleNameWithCaption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFUnusedInspectionTool extends LSFLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new PsiElementVisitor() {

            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof LSFSimpleNameWithCaption && !LSFReferenceAnnotator.isInMetaUsage(element)) {
                    LSFProblemsVisitor.visitLSFSimpleNameWithCaption(holder, element, false);
                }
            }
        };
    }

    @Nullable
    @Override
    public String getStaticDescription() {
        return "Reports unused properties, classes, groups.";
    }
}