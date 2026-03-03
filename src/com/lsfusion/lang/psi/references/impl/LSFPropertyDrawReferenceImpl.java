package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormExtendPropertyDrawNameUsageImpl;
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
        return decl -> {
            boolean isUsage = this instanceof LSFFormExtendPropertyDrawNameUsageImpl;
            LSFSimpleName declAlias = decl.getSimpleName();
            if (isUsage) {
                if (declAlias != null) {
                    return false;
                }
            } else {
                if (alias != null || declAlias != null) {
                    //сравниваем алиасы
                    //если алиаса нет у usage или decl, то этот decl не подходит
                    return alias != null && declAlias != null
                            && alias.getText().equals(declAlias.getText());
                }
            }

            assert propertyDrawName != null;

            String refName = propertyDrawName.getSimpleName().getText();
            LSFFormPropertyName formPropertyName = decl.getFormPropertyName();
            if (formPropertyName == null)
                return false;
            String declName = getNameIdentifier(formPropertyName).getText();

            LSFObjectUsageList objectUsageList = decl.getObjectUsageList();
            if (objectUsageList == null)
                return false;

            LSFObjectUsageList propertiesObjectUsageList;
            if (isUsage) {
                LSFFormMappedNamePropertiesList propertiesList = PsiTreeUtil.getParentOfType(this, LSFFormMappedNamePropertiesList.class);
                propertiesObjectUsageList = propertiesList != null ? propertiesList.getObjectUsageList() : null;
            } else {
                propertiesObjectUsageList = getObjectUsageList();
            }

            return refName != null && refName.equals(declName) &&
                    resolveEquals(propertiesObjectUsageList, objectUsageList);
        };
    }

    @Nullable
    public abstract LSFFormPropertyDrawPropertyUsage getFormPropertyDrawPropertyUsage();

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

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFPropertyDrawDeclaration;
    }
}
