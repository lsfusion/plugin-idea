package com.lsfusion.lang.typeinfer;

import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.lang.psi.LSFLikePE;
import com.lsfusion.lang.psi.LSFPsiImplUtil;

public class Relationed extends Compared<LSFLikePE> {

    public Relationed(LSFLikePE first, LSFLikePE second) {
        super(first, second);
    }

    @Override
    public LSFExClassSet resolveInferred(LSFLikePE operand, InferExResult inferred) {
        return LSFPsiImplUtil.resolveInferredValueClass(operand, inferred);
    }

    @Override
    public Inferred inferResolved(LSFLikePE operand, LSFExClassSet classSet) {
        return LSFPsiImplUtil.inferParamClasses(operand, classSet!=null && classSet.classSet instanceof DataClass ? classSet : null);
    }
}
