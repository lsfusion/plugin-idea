package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFGlobalPropDeclaration;
import com.simpleplugin.psi.stubs.PropStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class LSFGlobalPropDeclarationImpl extends LSFFullNameDeclarationImpl<LSFGlobalPropDeclaration, PropStubElement> implements LSFGlobalPropDeclaration {

    public LSFGlobalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFGlobalPropDeclarationImpl(@NotNull PropStubElement propStubElement, @NotNull IStubElementType nodeType) {
        super(propStubElement, nodeType);
    }

    protected abstract LSFPropertyDeclaration getPropertyDeclaration();

    @Nullable
    protected abstract LSFExpressionUnfriendlyPD getExpressionUnfriendlyPD();

    @Nullable
    protected abstract LSFPropertyExpression getPropertyExpression();


    @Override
    public LSFId getNameIdentifier() {
        return getPropertyDeclaration().getSimpleNameWithCaption().getSimpleName();
    }

    @Override
    public LSFClassSet resolveValueClass() {
        LSFExpressionUnfriendlyPD unfr = getExpressionUnfriendlyPD();
        if(unfr!=null)
            return unfr.resolveValueClass();

        LSFPropertyExpression expr = getPropertyExpression();
        if(expr!=null)
            return expr.resolveValueClass();

        return null; 
    }
    
    @Nullable
    private List<LSFClassSet> resolveValueParamClasses() {
        LSFExpressionUnfriendlyPD unfr = getExpressionUnfriendlyPD();
        if(unfr!=null)
            return unfr.resolveValueParamClasses();

        LSFPropertyExpression expr = getPropertyExpression();
        if(expr!=null)
            return expr.resolveValueParamClasses();

        return null;

    }

    @Override
    @Nullable
    public List<LSFClassSet> resolveParamClasses() {
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFClassParamDeclareList cpd = decl.getClassParamDeclareList();
        List<LSFClassSet> declareClasses = null;
        if(cpd!=null) {
            declareClasses = LSFPsiImplUtil.resolveClass(cpd);
            if(LSFPsiImplUtil.allClassesDeclared(declareClasses)) // оптимизация
                return declareClasses;
        }

        List<LSFClassSet> valueClasses = resolveValueParamClasses();
        if(valueClasses == null)
            return declareClasses;
            
        if(declareClasses == null)
            return valueClasses;

        List<LSFClassSet> mixed = new ArrayList<LSFClassSet>(declareClasses);
        for(int i=0,size=declareClasses.size();i<size;i++) {
            if(i >= valueClasses.size())
                break;
            
            if(declareClasses.get(i) == null)
                mixed.set(i, valueClasses.get(i));
        }
        return mixed;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return AllIcons.Nodes.Property;
    }
}
