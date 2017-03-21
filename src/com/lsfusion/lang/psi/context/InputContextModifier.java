package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.LSFParamDeclare;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class InputContextModifier implements ContextModifier {

    private final LSFExprParamDeclaration paramDeclare;

    public InputContextModifier(LSFExprParamDeclaration paramDeclare) {
        this.paramDeclare = paramDeclare;
    }

    @Override
    public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
        if(paramDeclare != null)
            return Collections.singletonList(paramDeclare);
        return new ArrayList<>();
    }
}
