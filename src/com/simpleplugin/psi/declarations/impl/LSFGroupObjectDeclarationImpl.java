package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFGroupObjectDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
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
}
