package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.refactoring.ElementMigration;
import com.lsfusion.refactoring.PropertyDrawMigration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public abstract class LSFPropertyDrawDeclarationImpl extends LSFFormElementDeclarationImpl<LSFPropertyDrawDeclaration> implements LSFPropertyDrawDeclaration {

    protected LSFPropertyDrawDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.PROPERTY_DRAW;
    }

    @Override
    public Condition getDuplicateCondition() {
        final LSFObjectUsageList objectUsageList = getObjectUsageList();
//        if (objectUsageList == null)
//            return super.getDuplicateCondition();
        
        final LSFSimpleName alias = getSimpleName();

        return (Condition<LSFPropertyDrawDeclaration>) decl -> {
            LSFSimpleName otherAlias = decl.getSimpleName();
            if (alias != null || otherAlias != null) {
                return alias != null && otherAlias != null && alias.getText().equals(otherAlias.getText());
            }

            LSFId nameIdentifier = getNameIdentifier();
            LSFId otherNameIdentifier = decl.getNameIdentifier();

            return nameIdentifier != null && otherNameIdentifier != null &&
                   nameIdentifier.getText().equals(otherNameIdentifier.getText()) &&
                   resolveEquals(objectUsageList, decl.getObjectUsageList());
        };
    }

    public static boolean resolveEquals(LSFObjectUsageList usage1, LSFObjectUsageList usage2) {
        List<LSFObjectUsage> list1 = LSFPsiImplUtil.getObjectUsageList(usage1);
        List<LSFObjectUsage> list2 = LSFPsiImplUtil.getObjectUsageList(usage2);
        if(list1.size() != list2.size())
            return false;

        for(int i=0,size=list1.size();i<size;i++)
            if(list1.get(i).resolveDecl() != list2.get(i).resolveDecl())
                return false;
        return true;

    }

    @Override
    public ElementMigration getMigration(String newName) {
        return new PropertyDrawMigration(this, newName);
    }
}
