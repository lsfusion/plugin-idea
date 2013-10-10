package com.simpleplugin.typeinfer;

import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.psi.LSFActionPropertyDefinition;
import com.simpleplugin.psi.context.ContextInferrer;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;

import java.util.Set;

public class ActionInferrer implements ContextInferrer {
    private final LSFActionPropertyDefinition actionDef;

    public ActionInferrer(LSFActionPropertyDefinition actionDef) {
        this.actionDef = actionDef;
    }

    public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
        return LSFPsiImplUtil.inferActionParamClasses(actionDef.getActionPropertyDefinitionBody(), params);
         
        /*{
            UnfriendlyPE unfriendlyPE = (UnfriendlyPE) actionDef.getContextIndependentPD().getChildren()[0];
            unfriendlyPE.inf
        }*/
    }
}
