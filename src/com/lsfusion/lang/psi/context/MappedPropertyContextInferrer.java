package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.LSFClassParamDeclareList;
import com.lsfusion.lang.psi.LSFMappedPropertyClassParamDeclare;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;

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
