package com.simpleplugin.typeinfer;

import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;

import java.util.HashMap;
import java.util.Map;

public interface InferResult {
    
    LSFClassSet get(LSFExprParamDeclaration decl);

    Map<LSFExprParamDeclaration, LSFClassSet> getMap();

    public final static InferResult EMPTY = new InferResult() {
        public LSFClassSet get(LSFExprParamDeclaration decl) {
            return null;
        }

        @Override
        public Map<LSFExprParamDeclaration, LSFClassSet> getMap() {
            return new HashMap<LSFExprParamDeclaration, LSFClassSet>();
        }
    };
}
