package com.simpleplugin.psi.context;

import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.typeinfer.Inferred;

import java.util.HashMap;
import java.util.Set;

public interface ContextInferrer {
    
    public final static ContextInferrer EMPTY = new ContextInferrer() {
        public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
            return Inferred.EMPTY;
        }
    };
    
    Inferred inferClasses(Set<LSFExprParamDeclaration> params);
}
