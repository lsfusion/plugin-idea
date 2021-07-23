package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ActionExpression extends PsiElement, LSFDocumentation {

    Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);
}
