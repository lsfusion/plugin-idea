package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.typeinfer.Inferred;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface ActionExpression extends PsiElement {

    Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);
}
