package com.lsfusion.lang.psi;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import org.jetbrains.annotations.NotNull;

public class LSFValueClassResolver implements LSFResolveCache.AbstractResolver<LSFPropDeclaration, LSFClassSet> {
    public static final LSFValueClassResolver INFER_INSTANCE = new LSFValueClassResolver(true);
    public static final LSFValueClassResolver NO_INFER_INSTANCE = new LSFValueClassResolver(false);

    private boolean infer;

    public LSFValueClassResolver(boolean infer) {
        this.infer = infer;
    }

    @Override
    public LSFClassSet resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
        return lsfPropDeclaration.resolveValueClassNoCache(infer);
    }
}
