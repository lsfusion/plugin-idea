package com.simpleplugin.typeinfer;

import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.DataClass;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.classes.SimpleDataClass;
import com.simpleplugin.psi.LSFAdditiveORPE;

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
