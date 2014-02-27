package com.lsfusion.lang.typeinfer;

import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFAdditiveORPE;
import com.lsfusion.lang.psi.LSFPsiImplUtil;

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
