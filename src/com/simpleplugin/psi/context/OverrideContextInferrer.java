package com.simpleplugin.psi.context;

import com.simpleplugin.BaseUtils;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.typeinfer.Inferred;

import java.util.*;

public class OverrideContextInferrer<T extends ContextInferrer> implements ContextInferrer {

    protected final List<? extends T> modifiers;

    public OverrideContextInferrer(List<? extends T> modifiers) {
        this.modifiers = modifiers;
    }

    public OverrideContextInferrer(T modifier1, T modifier2) {
        List<T> modifiers = new ArrayList<T>();
        modifiers.add(modifier1);
        modifiers.add(modifier2);

        this.modifiers = modifiers;
    }

    @Override
    public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
        Inferred result = Inferred.EMPTY;
        for(T modifier : modifiers)
            result = result.override(modifier.inferClasses(params));
        return result;
    }
}
