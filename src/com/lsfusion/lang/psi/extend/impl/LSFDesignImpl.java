package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.EmptyQuery;
import com.intellij.util.Query;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.declarations.impl.*;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.references.impl.LSFDesignElementReferenceImpl;
import com.lsfusion.lang.psi.stubs.extend.DesignStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.DesignStubElementType;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

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
    public LSFFullNameReference getExtendingReference() {
        return getDesignHeader().getFormUsage();
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
                    LSFMoveComponentStatement moveComponent = statement.getMoveComponentStatement();
                    if (moveComponent != null)
                        result.addAll(findComponents(moveComponent.getComponentBody()));
                    LSFNewComponentStatement newComponent = statement.getNewComponentStatement();
                    if(newComponent != null) {
                        result.addAll(findComponents(newComponent.getComponentBody()));
                        result.add(newComponent.getComponentDecl());
                    }
                }
            }
        }
        return result;
    }

    private static DesignStubElementType getContextExtendType() {
        return LSFStubElementTypes.DESIGN;
    }

    public static <T extends LSFDesignElementDeclaration<T>> Set<T> processDesignContext(PsiElement current, int offset, LSFLocalSearchScope localScope, final Function<LSFDesign, Collection<T>> processor) {
        return processContext(current, offset, localScope, processor,
                element -> element instanceof FormContext ? (FormContext)element : null,
                FormContext::resolveFormDecl, getContextExtendType());
    }

    protected List<Function<LSFDesign, Collection<? extends LSFDeclaration>>> getDuplicateProcessors() {
        List<Function<LSFDesign, Collection<? extends LSFDeclaration>>> processors = new ArrayList<>();
        processors.add(LSFDesign::getComponentDecls);
        return processors;
    }

    @Override
    protected ExtendStubElementType<LSFDesign, DesignStubElement> getDuplicateExtendType() {
        return getContextExtendType();
    }

    public Set<LSFDeclaration> resolveDuplicates() {
        return resolveDuplicates((Function<LSFDesignElementDeclaration, Condition<LSFDesignElementDeclaration>>) LSFDesignElementDeclaration::getDuplicateCondition, decl -> false);
    }
}