package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.stubs.extend.ExtendStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public abstract class LSFExtendImpl<This extends LSFExtend<This, Stub>, Stub extends ExtendStubElement<This, Stub>> extends LSFStubBasedPsiElement<This, Stub> implements LSFExtend<This, Stub> {

    public LSFExtendImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFExtendImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFFullNameDeclaration resolveDecl() {
        LSFFullNameDeclaration extendingDeclaration = resolveExtendingDeclaration();
        if (extendingDeclaration != null) {
            return extendingDeclaration;
        }

        LSFFile lsfFile = getLSFFile();
        if (lsfFile == lsfFile.getOriginalFile()) {
            return getOwnDeclaration();
        }

        String namespace = lsfFile.getModuleDeclaration().getNamespace();

        //noinspection RedundantTypeArguments
        return LSFResolveUtil.<LSFFullNameDeclaration>singleResolve(
                LSFGlobalResolver.findElements(getGlobalName(), namespace, lsfFile, getStubTypes(), Condition.TRUE, Finalizer.EMPTY)
        );
    }

    @Nullable
    protected abstract LSFFullNameDeclaration getOwnDeclaration();

    @Nullable
    protected abstract LSFFullNameDeclaration resolveExtendingDeclaration();

    protected Collection<FullNameStubElementType> getStubTypes() {
        return Collections.singleton(getStubType());
    }

    protected abstract FullNameStubElementType getStubType();
}
