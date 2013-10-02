package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFPropertyDrawDeclaration;
import com.simpleplugin.psi.declarations.impl.LSFPropertyDrawNameDeclarationImpl;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.references.LSFPropertyDrawReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class LSFPropertyDrawReferenceImpl extends LSFFormElementReferenceImpl<LSFPropertyDrawDeclaration> implements LSFPropertyDrawReference {

    public LSFPropertyDrawReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Processor<LSFPropertyDrawDeclaration> getProcessor() {
        return new Processor<LSFPropertyDrawDeclaration>() {
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
    protected Condition<LSFPropertyDrawDeclaration> getCondition() {
        final LSFFormMappedProperty mappedObject = getFormMappedProperty();
        if(mappedObject == null)
            return super.getCondition();
        
        return new Condition<LSFPropertyDrawDeclaration>() {
            public boolean value(LSFPropertyDrawDeclaration decl) {
                // проверим что resolve'ся туда же свойства и все объекты
                return resolveEquals(mappedObject.getFormPropertyName(), decl.getFormPropertyName()) && resolveEquals(mappedObject.getObjectUsageList(), decl.getObjectUsageList());
            }
        };
    }

    @Nullable
    protected abstract LSFCompoundID getCompoundID();

    @Nullable
    protected abstract LSFFormMappedProperty getFormMappedProperty();

    @Override
    public LSFId getSimpleName() {
        LSFFormMappedProperty mappedPropertyObject = getFormMappedProperty();
        if(mappedPropertyObject != null)
            return LSFPropertyDrawNameDeclarationImpl.getNameIdentifier(mappedPropertyObject.getFormPropertyName());
        return LSFFullNameReferenceImpl.getSimpleName(getCompoundID());
    }
}
