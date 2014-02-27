package com.lsfusion.lang.typeinfer;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.LSFRelationalPE;

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
