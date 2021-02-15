package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFCustomClassUsage;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFLocalSearchScope;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.impl.LSFClassExtendImpl;
import com.lsfusion.lang.psi.references.LSFStaticObjectReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class LSFStaticObjectReferenceImpl extends LSFReferenceImpl<LSFStaticObjectDeclaration> implements LSFStaticObjectReference {
    public LSFStaticObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFCustomClassUsage getCustomClassUsage();

    @Override
    public LSFResolveResult resolveNoCache() {
        String nameRef = getNameRef();

        List<LSFStaticObjectDeclaration> decls = new ArrayList<>();
        for(LSFStaticObjectDeclaration decl : LSFClassExtendImpl.processClassContext(getCustomClassUsage().resolveDecl(), getLSFFile(), getTextOffset(), LSFLocalSearchScope.createFrom(this), LSFClassExtend::getStaticObjects))
            if (BaseUtils.nullEquals(nameRef, decl.getDeclName()))
                decls.add(decl);

        return new LSFResolveResult(decls, resolveDefaultErrorAnnotator(decls, false));
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
