package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.LSFIcons;
import com.lsfusion.psi.LSFObjectUsageList;
import com.lsfusion.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.psi.extend.LSFFormExtend;
import com.lsfusion.psi.references.impl.LSFPropertyDrawReferenceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public abstract class LSFPropertyDrawDeclarationImpl extends LSFFormElementDeclarationImpl implements LSFPropertyDrawDeclaration {

    protected LSFPropertyDrawDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.PROPERTY_DRAW;
    }

    public static Processor getProcessor() {
        return new Processor<LSFPropertyDrawDeclaration>() {
            public Collection<LSFPropertyDrawDeclaration> process(LSFFormExtend formExtend) {
                return formExtend.getPropertyDrawDecls();
            }
        };
    }

    @Override
    public Condition getDuplicateCondition() {
        final LSFObjectUsageList objectUsageList = getObjectUsageList();
        if (objectUsageList == null)
            return super.getDuplicateCondition();

        return new Condition<LSFPropertyDrawDeclaration>() {
            public boolean value(LSFPropertyDrawDeclaration decl) {
                if (getSimpleName() == null && decl.getSimpleName() == null) {
                    return false;
                }
                return getNameIdentifier().getText().equals(decl.getNameIdentifier().getText()) &&
                        LSFPropertyDrawReferenceImpl.resolveEquals(objectUsageList, decl.getObjectUsageList());
            }
        };
    }
}
