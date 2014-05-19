package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFGroupObjectDeclarationImpl extends LSFFormElementDeclarationImpl implements LSFGroupObjectDeclaration {

    public LSFGroupObjectDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFFormMultiGroupObjectDeclaration getFormMultiGroupObjectDeclaration();

    protected abstract LSFFormSingleGroupObjectDeclaration getFormSingleGroupObjectDeclaration();

    @Override
    public List<LSFClassSet> resolveClasses() {
        List<LSFFormObjectDeclaration> objectDecls = new ArrayList<LSFFormObjectDeclaration>();
        LSFFormSingleGroupObjectDeclaration single = getFormSingleGroupObjectDeclaration();
        if (single != null)
            objectDecls.add(single.getFormObjectDeclaration());
        else
            objectDecls.addAll(getFormMultiGroupObjectDeclaration().getFormObjectDeclarationList());

        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for (LSFFormObjectDeclaration objectDecl : objectDecls)
            result.add(objectDecl.resolveClass());
        return result;
    }

    @Nullable
    public LSFId getNameIdentifier() {
        LSFFormSingleGroupObjectDeclaration single = getFormSingleGroupObjectDeclaration();
        if (single != null)
            return single.getFormObjectDeclaration().getNameIdentifier();
        LSFFormMultiGroupObjectDeclaration multi = getFormMultiGroupObjectDeclaration();
        LSFSimpleName name = multi.getSimpleName();
        if (name != null)
            return name;
        return null;
    }

    public Collection<LSFFormObjectDeclaration> findObjectDecarations() {
        return PsiTreeUtil.findChildrenOfType(this, LSFFormObjectDeclaration.class);
    }

    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.GROUP_OBJECT;
    }

    public static Processor<LSFGroupObjectDeclaration> getProcessor() {
        return new Processor<LSFGroupObjectDeclaration>() {
            public Collection<LSFGroupObjectDeclaration> process(LSFFormExtend formExtend) {
                return formExtend.getGroupObjectDecls();
            }
        };
    }
}
