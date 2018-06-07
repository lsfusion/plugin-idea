package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleNameWithCaption;
import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;
import com.lsfusion.lang.psi.extend.impl.LSFClassExtendImpl;
import com.lsfusion.refactoring.ElementMigration;
import com.lsfusion.refactoring.StaticObjectMigration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFStaticObjectDeclarationImpl extends LSFDeclarationImpl implements LSFStaticObjectDeclaration {
    public LSFStaticObjectDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFSimpleNameWithCaption getSimpleNameWithCaption();

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleNameWithCaption().getSimpleName();
    }

    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.STATIC_OBJECT;
    }

    @Override
    public ElementMigration getMigration(String newName) {
        return StaticObjectMigration.create(this, getClassNamespace(), getClassName(), getName(), newName);
    }
    
    private String getClassName() {
        LSFClassExtendImpl classExtend = PsiTreeUtil.getParentOfType(this, LSFClassExtendImpl.class);
        return classExtend == null ? null : classExtend.getClassName();
    }
    
    private String getClassNamespace() {
        LSFClassExtendImpl classExtend = PsiTreeUtil.getParentOfType(this, LSFClassExtendImpl.class);
        return classExtend == null ? null : classExtend.getClassNamespace();
    }
}
