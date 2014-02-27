package com.lsfusion.psi.context;

import com.lsfusion.psi.LSFExprParameterNameUsage;
import com.lsfusion.psi.LSFExprParameterUsage;
import com.lsfusion.psi.declarations.LSFExprParamDeclaration;

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
        Set<String> paramNames = new HashSet<String>();
        for(LSFExprParamDeclaration currentParam : currentParams)
            paramNames.add(currentParam.getDeclName());

        List<LSFExprParamDeclaration> result = new ArrayList<LSFExprParamDeclaration>();
        for(LSFExprParameterUsage paramUsage : paramUsages) {
            LSFExprParameterNameUsage nameUsage = paramUsage.getExprParameterNameUsage();
            if(nameUsage!=null && !paramNames.contains(nameUsage.getNameRef()))
                result.add(nameUsage.getClassParamDeclare().getParamDeclare());
        }
        return result;
    }
}
