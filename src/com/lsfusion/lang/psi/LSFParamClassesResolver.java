package com.lsfusion.lang.psi;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LSFParamClassesResolver implements LSFResolveCache.AbstractResolver<LSFPropDeclaration, List<LSFClassSet>> {
    public static final LSFParamClassesResolver INSTANCE = new LSFParamClassesResolver();

    @Override
    public List<LSFClassSet> resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
        return lsfPropDeclaration.resolveParamClassesNoCache();
    }
}
