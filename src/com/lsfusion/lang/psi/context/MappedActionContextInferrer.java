package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.psi.LSFClassParamDeclareList;
import com.lsfusion.lang.psi.LSFMappedActionClassParamDeclare;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;

public class MappedActionContextInferrer extends MappedActionOrPropertyContextInferrer<LSFActionDeclaration> {
    private final LSFMappedActionClassParamDeclare prop;

    public MappedActionContextInferrer(LSFMappedActionClassParamDeclare prop) {
        this.prop = prop;
    }

    @Override
    protected LSFActionDeclaration resolveDecl() {
        return prop.getActionUsageWrapper().getActionUsage().resolveDecl();
    }

    @Override
    protected LSFClassParamDeclareList getClassParamDeclareList() {
        return prop.getClassParamDeclareList();
    }
}
