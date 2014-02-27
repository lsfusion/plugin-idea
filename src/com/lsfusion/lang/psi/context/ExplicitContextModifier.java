package com.lsfusion.psi.context;

import com.lsfusion.util.BaseUtils;
import com.lsfusion.psi.LSFPsiImplUtil;
import com.lsfusion.psi.LSFClassParamDeclareList;
import com.lsfusion.psi.LSFMappedPropertyClassParamDeclare;
import com.lsfusion.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.psi.declarations.LSFParamDeclaration;

import java.util.List;
import java.util.Set;

public class ExplicitContextModifier implements ContextModifier {
    private final LSFClassParamDeclareList explicit;
    
    public ExplicitContextModifier(LSFMappedPropertyClassParamDeclare decl) {
        this(decl.getClassParamDeclareList());
    }
    
    public ExplicitContextModifier(LSFClassParamDeclareList explicit) {
        this.explicit = explicit;
    }

    @Override
    public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
        return BaseUtils.<LSFExprParamDeclaration, LSFParamDeclaration>immutableCast(LSFPsiImplUtil.resolveParams(explicit));
    }
}
