package com.simpleplugin.psi.context;

import com.simpleplugin.BaseUtils;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;

import java.util.*;

public class AndContextModifier implements ContextModifier {

    protected final List<? extends ContextModifier> modifiers;

    public AndContextModifier(List<? extends ContextModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public AndContextModifier(ContextModifier modifier1, ContextModifier modifier2) {
        this(BaseUtils.toList(modifier1, modifier2));
    }

    @Override
    public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
        LinkedHashSet<LSFExprParamDeclaration> extParams = new LinkedHashSet<LSFExprParamDeclaration>();
        for(ContextModifier modifier : modifiers)
            extParams.addAll(modifier.resolveParams(offset, BaseUtils.merge(currentParams, extParams)));
        return new ArrayList<LSFExprParamDeclaration>(extParams);
    }
}
