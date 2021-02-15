package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFPropertyDrawReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

import static com.lsfusion.lang.psi.declarations.impl.LSFPropertyDrawDeclarationImpl.resolveEquals;
import static com.lsfusion.lang.psi.declarations.impl.LSFPropertyDrawNameDeclarationImpl.getNameIdentifier;

public abstract class LSFPropertyDrawReferenceImpl extends LSFFormElementReferenceImpl<LSFPropertyDrawDeclaration> implements LSFPropertyDrawReference {

    public LSFPropertyDrawReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Function<LSFFormExtend, Collection<LSFPropertyDrawDeclaration>> getElementsCollector() {
        return LSFFormExtend::getPropertyDrawDecls;
    }

    @Override
    protected Condition<LSFPropertyDrawDeclaration> getResolvedDeclarationsFilter() {
        final LSFFormPropertyDrawPropertyUsage propertyDrawName = getFormPropertyDrawPropertyUsage();
        
        LSFAliasUsage aliasUsage = getAliasUsage();
        final LSFSimpleName alias = aliasUsage == null ? null : aliasUsage.getSimpleName();
        
        //usage через mapping
        return new Condition<LSFPropertyDrawDeclaration>() {
            public boolean value(LSFPropertyDrawDeclaration decl) {
                LSFSimpleName declAlias = decl.getSimpleName();
                if (alias != null || declAlias != null) {
                    //сравниваем алиасы
                    //если алиаса нет у usage или decl, то этот decl не подходит
                    return alias != null && declAlias != null 
                           && alias.getText().equals(declAlias.getText());
                }

                assert propertyDrawName != null;
                
                String refName = propertyDrawName.getSimpleName().getText();
                LSFFormPropertyName formPropertyName = decl.getFormPropertyName();
                if(formPropertyName == null)
                    return false;                
                String declName = getNameIdentifier(formPropertyName).getText();

                LSFObjectUsageList objectUsageList = decl.getObjectUsageList();
                if(objectUsageList == null)
                    return false;

                return refName != null && declName != null && refName.equals(declName) &&
                       resolveEquals(getObjectUsageList(), objectUsageList);
            }
        };
    }

    @Nullable
    public abstract LSFAliasUsage getAliasUsage();

    @Nullable
    public abstract LSFFormPropertyDrawPropertyUsage getFormPropertyDrawPropertyUsage();

    @Nullable
    public abstract LSFObjectUsageList getObjectUsageList();

    @Override
    public LSFId getSimpleName() {
        LSFFormPropertyDrawPropertyUsage propertyDrawName = getFormPropertyDrawPropertyUsage();
        if(propertyDrawName != null) {
            return propertyDrawName.getSimpleName();
        }

        LSFAliasUsage aliasUsage = getAliasUsage();

        assert aliasUsage != null;

        return aliasUsage.getSimpleName();
    }
}
