package com.lsfusion.lang.typeinfer;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.HashMap;
import java.util.Map;

public interface InferExResult {
    
    LSFExClassSet get(LSFExprParamDeclaration decl);

    Map<LSFExprParamDeclaration, LSFExClassSet> getMap();

    public final static InferExResult EMPTY = new InferExResult() {
        public LSFExClassSet get(LSFExprParamDeclaration decl) {
            return null;
        }

        @Override
        public Map<LSFExprParamDeclaration, LSFExClassSet> getMap() {
            return new HashMap<LSFExprParamDeclaration, LSFExClassSet>();
        }
    };
}
