package com.lsfusion.lang.typeinfer;

import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.LSFRelationalPE;

public class Equals extends Compared<LSFRelationalPE> {

    public Equals(LSFRelationalPE first, LSFRelationalPE second) {
        super(first, second);
    }

    @Override
    public LSFExClassSet resolveInferred(LSFRelationalPE operand, InferExResult inferred) {
        return LSFPsiImplUtil.resolveInferredValueClass(operand, inferred);
    }

    @Override
    public Inferred inferResolved(LSFRelationalPE operand, LSFExClassSet classSet) {
        return LSFPsiImplUtil.inferParamClasses(operand, classSet);
    }
}
