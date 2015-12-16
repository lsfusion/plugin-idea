package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFExprParameterNameUsage;
import com.lsfusion.lang.psi.LSFExprParameterUsage;
import com.lsfusion.lang.psi.LSFMappedPropertyExprParam;
import com.lsfusion.lang.psi.LSFMappedPropertyOrSimpleExprParam;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappedPropertyOrSimpleExprParamContextModifier extends ElementsContextModifier {

    private final List<LSFMappedPropertyOrSimpleExprParam> params;

    public MappedPropertyOrSimpleExprParamContextModifier(List<LSFMappedPropertyOrSimpleExprParam> params) {
        this.params = params;
    }

    protected List<? extends PsiElement> getElements() {
        return params;
    }
}
