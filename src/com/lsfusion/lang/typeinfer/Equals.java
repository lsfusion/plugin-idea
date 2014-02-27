package com.lsfusion.typeinfer;

import com.lsfusion.psi.LSFPsiImplUtil;
import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.LSFRelationalPE;

public class Equals extends Compared<LSFRelationalPE> {

    public Equals(LSFRelationalPE first, LSFRelationalPE second) {
        super(first, second);
    }

    @Override
    public LSFClassSet resolveInferred(LSFRelationalPE operand, InferResult inferred) {
        return LSFPsiImplUtil.resolveInferredValueClass(operand, inferred);
    }

    @Override
    public Inferred inferResolved(LSFRelationalPE operand, LSFClassSet classSet) {
        return LSFPsiImplUtil.inferParamClasses(operand, classSet);
    }
}
