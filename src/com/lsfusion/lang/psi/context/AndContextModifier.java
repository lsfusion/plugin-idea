package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.util.BaseUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        LinkedHashSet<LSFExprParamDeclaration> extParams = new LinkedHashSet<>();
        for(ContextModifier modifier : modifiers)
            extParams.addAll(modifier.resolveParams(offset, BaseUtils.merge(currentParams, extParams)));
        return new ArrayList<>(extParams);
    }
}
