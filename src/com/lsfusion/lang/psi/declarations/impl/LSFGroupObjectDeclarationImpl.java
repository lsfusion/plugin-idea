package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.LSFIcons;
import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.*;
import com.lsfusion.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.psi.extend.LSFFormExtend;
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
        if(single != null)
            objectDecls.add(single.getFormObjectDeclaration());
        else
            objectDecls.addAll(getFormMultiGroupObjectDeclaration().getFormObjectDeclarationList());
        
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for(LSFFormObjectDeclaration objectDecl : objectDecls)
            result.add(objectDecl.resolveClass());
        return result;            
    }

    @Nullable
    public LSFId getNameIdentifier() {
        LSFFormSingleGroupObjectDeclaration single = getFormSingleGroupObjectDeclaration();
        if(single != null)
            return single.getFormObjectDeclaration().getNameIdentifier();
        LSFFormMultiGroupObjectDeclaration multi = getFormMultiGroupObjectDeclaration();
        LSFSimpleName name = multi.getSimpleName();
        if(name!=null)
            return name;
        return null;    
    }

    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.GROUP_OBJECT;
    }

    public static Processor getProcessor() {
        return new Processor<LSFGroupObjectDeclaration>() {
            public Collection<LSFGroupObjectDeclaration> process(LSFFormExtend formExtend) {
                return formExtend.getGroupObjectDecls();
            }
        };
    }
}
