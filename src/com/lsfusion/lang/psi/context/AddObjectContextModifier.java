package com.lsfusion.psi.context;

import com.lsfusion.psi.LSFForAddObjClause;
import com.lsfusion.psi.LSFParamDeclare;
import com.lsfusion.psi.declarations.LSFExprParamDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AddObjectContextModifier implements ContextModifier {

    private final LSFForAddObjClause clause;

    public AddObjectContextModifier(LSFForAddObjClause clause) {
        this.clause = clause;
    }

    @Override
    public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
        LSFParamDeclare paramDeclare = clause.getParamDeclare();
        if(paramDeclare!=null)
            return Collections.<LSFExprParamDeclaration>singletonList(paramDeclare);
        return new ArrayList<LSFExprParamDeclaration>();
    }
}
