package com.lsfusion.lang.typeinfer;

import com.lsfusion.lang.psi.LSFActionPropertyDefinitionBody;
import com.lsfusion.lang.psi.LSFActionStatement;
import com.lsfusion.lang.psi.LSFActionUnfriendlyPD;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.Set;

public class ActionInferrer implements ContextInferrer {
    private final LSFActionPropertyDefinitionBody actionDef;

    public ActionInferrer(LSFActionPropertyDefinitionBody actionDef) {
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
