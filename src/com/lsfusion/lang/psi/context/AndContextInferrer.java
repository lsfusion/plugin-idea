package com.lsfusion.psi.context;

import com.lsfusion.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.typeinfer.Inferred;

import java.util.*;

public class AndContextInferrer implements ContextInferrer {

    protected final List<? extends ContextInferrer> modifiers;

    public AndContextInferrer(List<? extends ContextInferrer> modifiers) {
        this.modifiers = modifiers;
    }

    public AndContextInferrer(ContextInferrer modifier1, ContextInferrer modifier2) {
        List<ContextInferrer> modifiers = new ArrayList<ContextInferrer>();
        modifiers.add(modifier1);
        modifiers.add(modifier2);

        this.modifiers = modifiers;
    }

    @Override
    public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
        Inferred result = Inferred.EMPTY;
        for(ContextInferrer modifier : modifiers)
            result = result.and(modifier.inferClasses(params));
        return result;
    }

}
