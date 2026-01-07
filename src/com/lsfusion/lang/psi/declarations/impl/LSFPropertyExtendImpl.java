package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFMappedPropertyClassParamDeclare;
import com.lsfusion.lang.psi.LSFPropertyUsageWrapper;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyExtend;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.stubs.ExtendPropertyStubElement;
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

public class LSFPropertyExtendImpl extends LSFActionOrPropExtendImpl<LSFPropertyExtend, ExtendPropertyStubElement> implements LSFPropertyExtend {

    protected LSFPropertyExtendImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFPropertyExtendImpl(@NotNull ExtendPropertyStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getGlobalName() {
        ExtendPropertyStubElement stub = getStub();
        if (stub != null)
            return stub.getGlobalName();

        LSFMappedPropertyClassParamDeclare mapped = PsiTreeUtil.getChildOfType(this, LSFMappedPropertyClassParamDeclare.class);
        if (mapped != null) {
            LSFPropertyUsageWrapper wrapper = mapped.getPropertyUsageWrapper();
            if (wrapper != null) {
                return wrapper.getPropertyUsage().getNameRef();
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
        LSFMappedPropertyClassParamDeclare mapped = PsiTreeUtil.getChildOfType(this, LSFMappedPropertyClassParamDeclare.class);
        if (mapped != null) {
            LSFPropertyUsageWrapper wrapper = mapped.getPropertyUsageWrapper();
            if (wrapper != null) {
                return wrapper.getPropertyUsage();
            }
        }
        return null;
    }

    @Override
    protected Collection<FullNameStubElementType> getStubTypes() {
        return Arrays.asList(LSFStubElementTypes.STATEMENTPROP, LSFStubElementTypes.AGGRPARAMPROP);
    }

    @Override
    protected FullNameStubElementType getStubType() {
        return LSFStubElementTypes.STATEMENTPROP;
    }

    @Nullable
    public LSFId getNameIdentifier() {
        return null;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.PROPERTY;
    }

    @Override
    public java.util.Set<com.lsfusion.lang.psi.declarations.LSFDeclaration> resolveDuplicates() {
        return Collections.emptySet();
    }

    @Override
    protected List<Function<LSFPropertyExtend, Collection<? extends LSFDeclaration>>> getDuplicateProcessors() {
        return Collections.emptyList();
    }
}
