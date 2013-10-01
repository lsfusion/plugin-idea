package com.simpleplugin.psi.context;

import com.simpleplugin.BaseUtils;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.LSFClassParamDeclare;
import com.simpleplugin.psi.LSFClassParamDeclareList;
import com.simpleplugin.psi.LSFMappedPropertyClassParamDeclare;
import com.simpleplugin.psi.LSFNonEmptyClassParamDeclareList;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.psi.declarations.LSFParamDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
