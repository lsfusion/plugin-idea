package com.simpleplugin.psi.context;

import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface ContextModifier {
    
    public static final ContextModifier EMPTY = new ContextModifier() {
        public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
            return Collections.emptyList();
        }
    };

    public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams);
}
