package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.LSFPropertyObject;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ContextModifier {
    
    public static final ContextModifier EMPTY = new ContextModifier() {
        public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
            return new ArrayList<LSFExprParamDeclaration>();
        }
    };

    public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams);
}
