package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFMappedPropertyOrSimpleExprParam;

import java.util.List;

public class MappedPropertyOrSimpleExprParamContextModifier extends ElementsContextModifier {

    private final List<LSFMappedPropertyOrSimpleExprParam> params;

    public MappedPropertyOrSimpleExprParamContextModifier(List<LSFMappedPropertyOrSimpleExprParam> params) {
        this.params = params;
    }

    protected List<? extends PsiElement> getElements() {
        return params;
    }
}
