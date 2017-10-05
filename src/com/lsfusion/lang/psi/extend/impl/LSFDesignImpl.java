package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFComponentDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.stubs.extend.DesignStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFDesignImpl extends LSFExtendImpl<LSFDesign, DesignStubElement> implements LSFDesign {

    public LSFDesignImpl(@NotNull DesignStubElement designStubElement, @NotNull IStubElementType nodeType) {
        super(designStubElement, nodeType);
    }

    public LSFDesignImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public abstract LSFDesignHeader getDesignHeader();

    @Nullable
    public abstract LSFComponentBody getComponentBody();

    @Override
    public String getGlobalName() {
        DesignStubElement stub = getStub();
        if (stub != null)
            return stub.getGlobalName();

        return getDesignHeader().getFormUsage().getNameRef();
    }

    @Override
    protected LSFFormDeclaration getOwnDeclaration() {
        return null;
    }

    @Override
    public LSFId getFormUsageNameIdentifier() {
        return getDesignHeader().getFormUsage().getCompoundID().getSimpleName();
    }
    
    @Nullable
    @Override
    protected LSFFormDeclaration resolveExtendingDeclaration() {
        return getDesignHeader().getFormUsage().resolveDecl();
    }

    @Override
    protected FullNameStubElementType getStubType() {
        return LSFStubElementTypes.FORM;
    }

    @Override
    public Collection<LSFComponentDeclaration> getComponentDecls() {
        return findComponents(getComponentBody());
    }

    public Collection<LSFComponentDeclaration> findComponents(LSFComponentBody parentComponentBody) {
        Collection<LSFComponentDeclaration> result = new ArrayList<>();
        if(parentComponentBody != null) {
            LSFComponentBlockStatement componentBlockStatement = parentComponentBody.getComponentBlockStatement();
            if (componentBlockStatement != null) {
                List<LSFComponentStatement> componentStatementList = componentBlockStatement.getComponentStatementList();
                for (LSFComponentStatement statement : componentStatementList) {
                    LSFSetupComponentStatement setupComponent = statement.getSetupComponentStatement();
                    if (setupComponent != null)
                        result.addAll(findComponents(setupComponent.getComponentBody()));
                    LSFNewComponentStatement newComponent = statement.getNewComponentStatement();
                    if(newComponent != null) {
                        result.addAll(findComponents(newComponent.getComponentBody()));
                        result.add(newComponent.getComponentStubDecl().getComponentDecl());
                    }
                }
            }
        }
        return result;
    }
}