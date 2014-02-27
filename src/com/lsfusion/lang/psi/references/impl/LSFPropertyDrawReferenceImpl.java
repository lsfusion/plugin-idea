package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFPropertyDrawNameDeclarationImpl;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFPropertyDrawReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class LSFPropertyDrawReferenceImpl extends LSFFormElementReferenceImpl<LSFPropertyDrawDeclaration> implements LSFPropertyDrawReference {

    public LSFPropertyDrawReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FormExtendProcessor<LSFPropertyDrawDeclaration> getElementsCollector() {
        return new FormExtendProcessor<LSFPropertyDrawDeclaration>() {
            public Collection<LSFPropertyDrawDeclaration> process(LSFFormExtend formExtend) {
                return formExtend.getPropertyDrawDecls();
            }
        };
    }

    public static boolean resolveEquals(LSFPropertyUsage usage1, LSFPropertyUsage usage2) {
        if(!usage1.getNameRef().equals(usage2.getNameRef())) // оптимизация
            return false;
        
        return usage1.resolveDecl() == usage2.resolveDecl();
    }

    public static boolean resolveEquals(LSFFormPropertyName name1, LSFFormPropertyName name2) {
        LSFPredefinedFormPropertyName predef1 = name1.getPredefinedFormPropertyName();
        LSFPredefinedFormPropertyName predef2 = name2.getPredefinedFormPropertyName();
        if(predef1!=null)
            return predef2 != null && predef1.getText().equals(predef2.getText());
        return predef2 == null && resolveEquals(name1.getPropertyUsage(), name2.getPropertyUsage());
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
    protected Condition<LSFPropertyDrawDeclaration> getResolvedDeclarationsFilter() {
        final LSFFormPropertyObject propertyObject = getFormPropertyObject();
        if(propertyObject == null)
            return super.getResolvedDeclarationsFilter();
        
        return new Condition<LSFPropertyDrawDeclaration>() {
            public boolean value(LSFPropertyDrawDeclaration decl) {
                // проверим что resolve'ся туда же свойства и все объекты
                return resolveEquals(propertyObject.getFormPropertyName(), decl.getFormPropertyName()) && resolveEquals(propertyObject.getObjectUsageList(), decl.getObjectUsageList());
            }
        };
    }

    @Nullable
    protected abstract LSFAliasUsage getAliasUsage();

    @Nullable
    protected abstract LSFFormPropertyObject getFormPropertyObject();

    @Override
    public LSFId getSimpleName() {
        LSFFormPropertyObject formPropertyObject = getFormPropertyObject();
        if(formPropertyObject != null)
            return LSFPropertyDrawNameDeclarationImpl.getNameIdentifier(formPropertyObject.getFormPropertyName());

        LSFAliasUsage aliasUsage = getAliasUsage();

        assert aliasUsage != null;

        return aliasUsage.getSimpleName();
    }
}
