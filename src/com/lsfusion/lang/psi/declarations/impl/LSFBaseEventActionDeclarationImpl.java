package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFBaseEventActionDeclaration;
import com.lsfusion.lang.psi.stubs.BaseEventActionStubElement;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;
// здесь не надо городить огород со стабами так как тут все явно и можно использовать одну реализацию как для heavy так и для light
public abstract class LSFBaseEventActionDeclarationImpl extends LSFFullNameDeclarationImpl<LSFBaseEventActionDeclaration, BaseEventActionStubElement> implements LSFBaseEventActionDeclaration {

    public LSFBaseEventActionDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFBaseEventActionDeclarationImpl(@NotNull BaseEventActionStubElement actionStubElement, @NotNull IStubElementType nodeType) {
        super(actionStubElement, nodeType);
    }

    protected abstract LSFParamDeclare getParamDeclare();
    
    @Nullable
    @Override
    public LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions() {
        return null;
    }

    @Override
    public @Nullable LSFNonEmptyActionOptions getNonEmptyActionOptions() {
        return null;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public String getValuePresentableText() {
        return getParamDeclare().getText();
    }

    @Override
    public boolean isAction() {
        return true;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public Icon getIcon(int flags) {
        return LSFActionOrGlobalPropDeclarationImpl.getIcon(getPropType());
    }

    @Override
    public LSFExplicitClasses getExplicitParams() {
        return new LSFExplicitSignature(Collections.emptyList());
    }

    @Override
    public Set<LSFActionOrGlobalPropDeclaration<?, ?>> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public List<LSFExClassSet> resolveExParamClassesNoCache() {
        return Collections.emptyList();
    }
    
    @Nullable
    @Override
    public List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass) {
        return Collections.emptyList();
    }

    @Override
    public boolean isNoParams() {
        return true;
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        LSFParamDeclare paramDeclare = getParamDeclare();
        return paramDeclare != null ? paramDeclare.getNameIdentifier() : null;
    }
}
