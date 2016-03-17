package com.lsfusion.lang.typeinfer;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.HashMap;
import java.util.Map;

public interface InferResult {
    
    LSFClassSet get(LSFExprParamDeclaration decl);

    Map<LSFExprParamDeclaration, LSFClassSet> getMap();

    InferResult EMPTY = new InferResult() {
        public LSFClassSet get(LSFExprParamDeclaration decl) {
            return null;
        }

        @Override
        public Map<LSFExprParamDeclaration, LSFClassSet> getMap() {
            return new HashMap<>();
        }
    };
}
