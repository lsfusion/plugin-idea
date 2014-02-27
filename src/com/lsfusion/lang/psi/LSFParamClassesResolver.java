package com.lsfusion.psi;

import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.declarations.LSFPropDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LSFParamClassesResolver implements LSFResolveCache.AbstractResolver<LSFPropDeclaration, List<LSFClassSet>> {
    public static final LSFParamClassesResolver INSTANCE = new LSFParamClassesResolver();

    @Override
    public List<LSFClassSet> resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
        return lsfPropDeclaration.resolveParamClassesNoCache();
    }
}
