package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.LSFExprParameterNameUsage;
import com.lsfusion.lang.psi.LSFExprParameterUsage;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExprParamUsageContextModifier implements ContextModifier {

    private final List<LSFExprParameterUsage> paramUsages;

    public ExprParamUsageContextModifier(List<LSFExprParameterUsage> paramUsages) {
        this.paramUsages = paramUsages;
    }

    @Override
    public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
        Set<String> paramNames = new HashSet<>();
        for(LSFExprParamDeclaration currentParam : currentParams)
            paramNames.add(currentParam.getDeclName());

        List<LSFExprParamDeclaration> result = new ArrayList<>();
        for(LSFExprParameterUsage paramUsage : paramUsages) {
            if(paramUsage.getTextOffset() > offset) // we don't need param usages after resolving offset
                continue;

            LSFExprParameterNameUsage nameUsage = paramUsage.getExprParameterNameUsage();
            if(!paramNames.contains(nameUsage.getNameRef()))
                result.add(nameUsage.getClassParamDeclare().getParamDeclare());
        }
        return result;
    }
}
