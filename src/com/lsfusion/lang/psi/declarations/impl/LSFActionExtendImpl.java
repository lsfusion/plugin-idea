package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFMappedActionClassParamDeclare;
import com.lsfusion.lang.psi.LSFActionUsageWrapper;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFActionExtend;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.stubs.ExtendActionStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class LSFActionExtendImpl extends LSFActionOrPropExtendImpl<LSFActionExtend, ExtendActionStubElement> implements LSFActionExtend {

    protected LSFActionExtendImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFActionExtendImpl(@NotNull ExtendActionStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getGlobalName() {
        ExtendActionStubElement stub = getStub();
        if (stub != null)
            return stub.getGlobalName();

        LSFMappedActionClassParamDeclare mapped = PsiTreeUtil.getChildOfType(this, LSFMappedActionClassParamDeclare.class);
        if (mapped != null) {
            LSFActionUsageWrapper wrapper = mapped.getActionUsageWrapper();
            if (wrapper != null) {
                return wrapper.getActionUsage().getNameRef();
            }
        }
        return null;
    }

    @Nullable
    @Override
    protected com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration getOwnDeclaration() {
        return null;
    }

    @Nullable
    @Override
    public LSFFullNameReference getExtendingReference() {
        LSFMappedActionClassParamDeclare mapped = PsiTreeUtil.getChildOfType(this, LSFMappedActionClassParamDeclare.class);
        if (mapped != null) {
            LSFActionUsageWrapper wrapper = mapped.getActionUsageWrapper();
            if (wrapper != null) {
                return wrapper.getActionUsage();
            }
        }
        return null;
    }

    @Override
    protected Collection<FullNameStubElementType> getStubTypes() {
        return Arrays.asList(LSFStubElementTypes.STATEMENTACTION, LSFStubElementTypes.BASEEVENTACTION);
    }

    @Override
    protected FullNameStubElementType getStubType() {
        return LSFStubElementTypes.STATEMENTACTION;
    }

    @Nullable
    public LSFId getNameIdentifier() {
        return null;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.ACTION;
    }

    @Override
    public java.util.Set<com.lsfusion.lang.psi.declarations.LSFDeclaration> resolveDuplicates() {
        return Collections.emptySet();
    }

    @Override
    protected List<Function<LSFActionExtend, Collection<? extends LSFDeclaration>>> getDuplicateProcessors() {
        return Collections.emptyList();
    }
}