package com.lsfusion.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.lsfusion.lang.psi.LSFAssignActionPropertyDefinitionBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFTypeMismatchInspectionTool extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new LSFElementVisitor() {
            @Override
            public void visit(PsiElement element) {
                if (element instanceof LSFAssignActionPropertyDefinitionBody) {
                    LSFProblemsVisitor.visitLSFAssignActionPropertyDefinitionBody(holder, (LSFAssignActionPropertyDefinitionBody) element, false);
                }
            }
        };
    }

    @Nullable
    @Override
    public String getStaticDescription() {
        return "Reports unsafe class cast in assign.";
    }
}