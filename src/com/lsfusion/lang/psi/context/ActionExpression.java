package com.lsfusion.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.typeinfer.Inferred;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ActionExpression extends PsiElement {

    Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);
}
