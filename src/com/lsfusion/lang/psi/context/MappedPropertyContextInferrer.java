package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.util.BaseUtils;

import java.util.*;

public class MappedPropertyContextInferrer extends MappedActionOrPropertyContextInferrer<LSFPropDeclaration> {
    private final LSFMappedPropertyClassParamDeclare prop;

    public MappedPropertyContextInferrer(LSFMappedPropertyClassParamDeclare prop) {
        this.prop = prop;
    }

    @Override
    protected LSFPropDeclaration resolveDecl() {
        return prop.getPropertyUsageWrapper().getPropertyUsage().resolveDecl();
    }

    @Override
    protected LSFClassParamDeclareList getClassParamDeclareList() {
        return prop.getClassParamDeclareList();
    }
}
