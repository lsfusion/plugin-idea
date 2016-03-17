package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;

import java.util.Set;

public interface ContextInferrer {
    
    ContextInferrer EMPTY = new ContextInferrer() {
        public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
            return Inferred.EMPTY;
        }
    };
    
    Inferred inferClasses(Set<LSFExprParamDeclaration> params);
}
