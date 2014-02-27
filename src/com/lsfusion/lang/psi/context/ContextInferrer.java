package com.lsfusion.psi.context;

import com.lsfusion.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.typeinfer.Inferred;

import java.util.Set;

public interface ContextInferrer {
    
    public final static ContextInferrer EMPTY = new ContextInferrer() {
        public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
            return Inferred.EMPTY;
        }
    };
    
    Inferred inferClasses(Set<LSFExprParamDeclaration> params);
}
