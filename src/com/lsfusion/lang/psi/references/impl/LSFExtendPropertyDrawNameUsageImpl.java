package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFPropertyDrawReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

import static com.lsfusion.lang.psi.declarations.impl.LSFPropertyDrawDeclarationImpl.resolveEquals;
import static com.lsfusion.lang.psi.declarations.impl.LSFPropertyDrawNameDeclarationImpl.getNameIdentifier;

public abstract class LSFExtendPropertyDrawNameUsageImpl extends LSFFormElementReferenceImpl<LSFPropertyDrawDeclaration> implements LSFPropertyDrawReference {

    public LSFExtendPropertyDrawNameUsageImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Function<LSFFormExtend, Collection<LSFPropertyDrawDeclaration>> getElementsCollector() {
        return LSFFormExtend::getPropertyDrawDecls;
    }

    @Override
    protected Condition<LSFPropertyDrawDeclaration> getResolvedDeclarationsFilter() {
        final LSFFormPropertyDrawPropertyUsage propertyDrawName = getFormPropertyDrawPropertyUsage();
        return decl -> {
            LSFSimpleName declAlias = decl.getSimpleName();
            if (declAlias != null) {
                return false;
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

            return refName != null && refName.equals(declName) &&
                   resolveEquals(getObjectUsageList(), objectUsageList);
        };
    }

    @Override
    public LSFAliasUsage getAliasUsage() {
        return null;
    }

    @Override
    public LSFObjectUsageList getObjectUsageList() {
        LSFFormExtendMappedNamePropertiesList propertiesList = PsiTreeUtil.getParentOfType(this, LSFFormExtendMappedNamePropertiesList.class);
        return propertiesList != null ? propertiesList.getObjectUsageList() : null;
    }

    public LSFFormPropertyDrawPropertyUsage getFormPropertyDrawPropertyUsage() {
        return PsiTreeUtil.getChildOfType(this, LSFFormPropertyDrawPropertyUsage.class);
    }

    public LSFSimpleName getSimpleName() {
        return ((LSFFormExtendPropertyDrawNameUsage) this).getFormPropertyDrawPropertyUsage().getSimpleName();
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFPropertyDrawDeclaration;
    }
}
