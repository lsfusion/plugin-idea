package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.LSFExprParameterUsage;
import com.lsfusion.lang.psi.LSFMappedPropertyExprParam;
import com.lsfusion.lang.psi.LSFMappedPropertyOrSimpleExprParam;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MappedPropertyOrSimpleExprParamContextInferrer implements ContextInferrer {

    private static class ExprParamUsageInferrer implements ContextInferrer {
        private final LSFExprParameterUsage paramUsage;

        public ExprParamUsageInferrer(LSFExprParameterUsage paramUsage) {
            this.paramUsage = paramUsage;
        }

        @Override
        public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
            return LSFPsiImplUtil.inferParamClasses(paramUsage, null);
        }
    }

    private static class MappedPropertyExprParamInferrer implements ContextInferrer {
        private final LSFMappedPropertyExprParam usage;

        public MappedPropertyExprParamInferrer(LSFMappedPropertyExprParam usage) {
            this.usage = usage;
        }

        @Override
        public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
            return LSFPsiImplUtil.inferParamClasses(usage, null);
        }
    }

    private final ContextInferrer andInferrer;

    public MappedPropertyOrSimpleExprParamContextInferrer(List<LSFMappedPropertyOrSimpleExprParam> params) {
        List<ContextInferrer> inferrers = new ArrayList<>();
        for(LSFMappedPropertyOrSimpleExprParam param : params) {
            LSFExprParameterUsage exprParameterUsage = param.getExprParameterUsage();
            ContextInferrer inferrer;
            if(exprParameterUsage != null) {
                inferrer = new ExprParamUsageInferrer(exprParameterUsage);
            } else {
                inferrer = new MappedPropertyExprParamInferrer(param.getMappedPropertyExprParam());
            }
            inferrers.add(inferrer);
        }
        andInferrer = new AndContextInferrer(inferrers);
    }

    @Override
    public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
        return andInferrer.inferClasses(params);
    }
}
