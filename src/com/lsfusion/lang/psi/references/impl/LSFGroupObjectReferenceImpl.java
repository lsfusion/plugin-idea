package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.psi.extend.LSFFormExtend;
import com.lsfusion.psi.references.LSFGroupObjectReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class LSFGroupObjectReferenceImpl extends LSFFormElementReferenceImpl<LSFGroupObjectDeclaration> implements LSFGroupObjectReference {
    protected LSFGroupObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FormExtendProcessor<LSFGroupObjectDeclaration> getElementsCollector() {
        return new FormExtendProcessor<LSFGroupObjectDeclaration>() {
            public Collection<LSFGroupObjectDeclaration> process(LSFFormExtend formExtend) {
                return formExtend.getGroupObjectDecls();
            }
        };
    }

    @Nullable
    public List<LSFClassSet> resolveClasses() {
        LSFGroupObjectDeclaration decl = resolveDecl();
        if(decl == null)
            return null;
        return decl.resolveClasses();
    }

}
