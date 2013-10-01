package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.declarations.LSFGroupObjectDeclaration;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.references.LSFGroupObjectReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class LSFGroupObjectReferenceImpl extends LSFFormElementReferenceImpl<LSFGroupObjectDeclaration> implements LSFGroupObjectReference {
    protected LSFGroupObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Processor<LSFGroupObjectDeclaration> getProcessor() {
        return new Processor<LSFGroupObjectDeclaration>() {
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
