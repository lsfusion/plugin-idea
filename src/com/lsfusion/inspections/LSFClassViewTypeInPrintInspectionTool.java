package com.lsfusion.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.lsfusion.lang.psi.LSFAssignActionPropertyDefinitionBody;
import com.lsfusion.lang.psi.LSFPrintActionPropertyDefinitionBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFClassViewTypeInPrintInspectionTool extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new LSFElementVisitor() {
            @Override
            public void visit(PsiElement element) {
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
