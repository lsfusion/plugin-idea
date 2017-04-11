package com.lsfusion.lang.typeinfer;

import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.Set;

public class ActionInferrer implements ContextInferrer {
    private final LSFListActionPropertyDefinitionBody actionDef;

    public ActionInferrer(LSFListActionPropertyDefinitionBody actionDef) {
        this.actionDef = actionDef;
    }

    public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
        return LSFPsiImplUtil.inferActionParamClasses(actionDef, params);
        /*{
            UnfriendlyPE unfriendlyPE = (UnfriendlyPE) actionDef.getContextIndependentPD().getChildren()[0];
            unfriendlyPE.inf
        }*/
    }
}
