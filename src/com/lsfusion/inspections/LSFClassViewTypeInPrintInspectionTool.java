package com.lsfusion.inspections;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.lsfusion.lang.psi.LSFPrintActionPropertyDefinitionBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFClassViewTypeInPrintInspectionTool extends LSFLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new PsiElementVisitor() {

            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof LSFPrintActionPropertyDefinitionBody) {
                    LSFProblemsVisitor.visitLSFPrintActionPropertyDefinitionBody(holder, (LSFPrintActionPropertyDefinitionBody) element, false);
                }
            }
        };
    }

    @Nullable
    @Override
    public String getStaticDescription() {
        return "Reports incorrect ClassViewType for objects in PRINT.";
    }
}
