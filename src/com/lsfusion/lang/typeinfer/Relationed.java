package com.lsfusion.typeinfer;

import com.lsfusion.psi.LSFPsiImplUtil;
import com.lsfusion.classes.DataClass;
import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.LSFAdditiveORPE;

public class Relationed extends Compared<LSFAdditiveORPE> {

    public Relationed(LSFAdditiveORPE first, LSFAdditiveORPE second) {
        super(first, second);
    }

    @Override
    public LSFClassSet resolveInferred(LSFAdditiveORPE operand, InferResult inferred) {
        return LSFPsiImplUtil.resolveInferredValueClass(operand, inferred);
    }

    @Override
    public Inferred inferResolved(LSFAdditiveORPE operand, LSFClassSet classSet) {
        return LSFPsiImplUtil.inferParamClasses(operand, classSet!=null && classSet instanceof DataClass ? classSet : null);
    }
}
