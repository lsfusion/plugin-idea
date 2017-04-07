package com.lsfusion.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class LSFLocalInspectionTool extends LocalInspectionTool {

    @NotNull
    @Override
    public List<ProblemDescriptor> processFile(@NotNull PsiFile file, @NotNull InspectionManager manager) {
        if(file.isWritable())
            return super.processFile(file, manager);
        else return Collections.emptyList();
    }
}
