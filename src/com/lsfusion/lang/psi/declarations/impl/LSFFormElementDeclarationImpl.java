package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.psi.declarations.LSFDeclaration;
import com.lsfusion.psi.declarations.LSFFormElementDeclaration;
import com.lsfusion.psi.extend.LSFFormExtend;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class LSFFormElementDeclarationImpl<T extends LSFDeclaration> extends LSFDeclarationImpl implements LSFFormElementDeclaration, LSFFormExtendElement {

    public static interface Processor<T> {
        Collection<T> process(LSFFormExtend formExtend);
    }

    protected LSFFormElementDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Condition<T> getDuplicateCondition() {
        final String declName = getDeclName();
        return declName == null
               ? Condition.FALSE
               :
               new Condition<T>() {
                   public boolean value(T decl) {
                       return declName.equals(decl.getDeclName());
                   }
               };
    }
}
