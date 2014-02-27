package com.lsfusion.typeinfer;

import com.lsfusion.psi.LSFPsiImplUtil;
import com.lsfusion.psi.LSFActionPropertyDefinition;
import com.lsfusion.psi.context.ContextInferrer;
import com.lsfusion.psi.declarations.LSFExprParamDeclaration;

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
