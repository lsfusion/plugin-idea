package com.simpleplugin.typeinfer;

import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.LSFActionPropertyDefinition;
import com.simpleplugin.psi.LSFExpressionUnfriendlyPD;
import com.simpleplugin.psi.context.ContextInferrer;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;

import java.util.HashMap;
import java.util.Set;

public class UnfriendlyInferrer implements ContextInferrer {
    private final LSFExpressionUnfriendlyPD unfriendlyPD;

    public UnfriendlyInferrer(LSFExpressionUnfriendlyPD unfriendlyPD) {
        this.unfriendlyPD = unfriendlyPD;
    }

    public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
        if(unfriendlyPD == null) // native
            return Inferred.EMPTY;
        
        LSFActionPropertyDefinition actionDef = unfriendlyPD.getActionPropertyDefinition();
        if(actionDef != null)
            return LSFPsiImplUtil.inferActionParamClasses(actionDef.getActionPropertyDefinitionBody(), params);
        else
            return Inferred.EMPTY; // нет смысла выводить так как избыточны
         
        /*{
            UnfriendlyPE unfriendlyPE = (UnfriendlyPE) unfriendlyPD.getContextIndependentPD().getChildren()[0];
            unfriendlyPE.inf
        }*/
    }
}
