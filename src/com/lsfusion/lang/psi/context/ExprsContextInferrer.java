package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.LSFPropertyExpression;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExprsContextInferrer implements ContextInferrer {

    private final List<LSFPropertyExpression> exprs;

    public ExprsContextInferrer(LSFPropertyExpression expr) {
        this.exprs = Collections.singletonList(expr);
    }

    public ExprsContextInferrer(List<LSFPropertyExpression> exprs) {
        this.exprs = exprs;
    }

    @Override
    public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
        Inferred result = Inferred.EMPTY;
        for(LSFPropertyExpression expr : exprs)
            result = result.and(expr.inferParamClasses(null).filter(params));
        return result;
    }
}
