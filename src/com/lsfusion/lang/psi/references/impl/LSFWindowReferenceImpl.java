package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.declarations.LSFWindowDeclaration;
import com.lsfusion.lang.psi.references.LSFWindowReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.stubs.types.WindowStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public abstract class LSFWindowReferenceImpl extends LSFFullNameReferenceImpl<LSFWindowDeclaration, LSFWindowDeclaration> implements LSFWindowReference {
    
    public LSFWindowReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected WindowStubElementType getStubElementType() {
        return LSFStubElementTypes.WINDOW;
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        Collection<? extends LSFWindowDeclaration> builtInWindows = LSFElementGenerator.getBuiltInWindows(getProject());
        LSFWindowDeclaration builtInWndsArr[] = builtInWindows.toArray(new LSFWindowDeclaration[builtInWindows.size()]);
        Collection<LSFWindowDeclaration> decls =
                LSFGlobalResolver.findElements(
                        getNameRef(),
                        getFullNameRef(),
                        getLSFFile(),
                        Collections.singleton(LSFStubElementTypes.WINDOW),
                        getCondition(),
                        getFinalizer(),
                        builtInWndsArr);
        return new LSFResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }
}
