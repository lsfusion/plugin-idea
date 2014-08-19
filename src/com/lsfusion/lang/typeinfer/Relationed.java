package com.lsfusion.lang.typeinfer;

import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.lang.psi.LSFAdditiveORPE;
import com.lsfusion.lang.psi.LSFPsiImplUtil;

public class Relationed extends Compared<LSFAdditiveORPE> {

    public Relationed(LSFAdditiveORPE first, LSFAdditiveORPE second) {
        super(first, second);
    }

    @Override
    public LSFExClassSet resolveInferred(LSFAdditiveORPE operand, InferExResult inferred) {
        return LSFPsiImplUtil.resolveInferredValueClass(operand, inferred);
    }

    @Override
    public Inferred inferResolved(LSFAdditiveORPE operand, LSFExClassSet classSet) {
        return LSFPsiImplUtil.inferParamClasses(operand, classSet!=null && classSet.classSet instanceof DataClass ? classSet : null);
    }
}
