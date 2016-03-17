package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface ContextModifier {
    
    ContextModifier EMPTY = new ContextModifier() {
        public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
            return Collections.emptyList();
        }
    };

    List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams);
}
