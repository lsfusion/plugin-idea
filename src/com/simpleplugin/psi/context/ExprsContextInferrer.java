package com.simpleplugin.psi.context;

import com.simpleplugin.psi.LSFPropertyExpression;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.typeinfer.Inferred;

import java.util.*;

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
