package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.Query;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.declarations.impl.*;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.impl.LSFDesignElementReferenceImpl;
import com.lsfusion.lang.psi.stubs.extend.DesignStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    public Set<LSFDeclaration> resolveDuplicates() {
        Query<LSFDesign> designs = LSFGlobalResolver.findExtendElements(resolveDecl(), LSFStubElementTypes.DESIGN, getLSFFile());

        Set<LSFDeclaration> duplicates = new LinkedHashSet<>();
        duplicates.addAll(resolveDuplicates((List<LSFComponentDeclaration>) getComponentDecls(), LSFComponentDeclarationImpl.getProcessor(), designs));

        return duplicates;
    }

    private <T extends LSFDesignElementDeclaration<T>> Set<T> resolveDuplicates(List<T> localDecls, final LSFDesignElementReferenceImpl.DesignProcessor<T> processor, Query<LSFDesign> designs) {
        Set<T> duplicates = new LinkedHashSet<>();

        for (int i = 0; i < localDecls.size(); i++) {
            T decl1 = localDecls.get(i);
            Condition<T> decl1Condition = decl1.getDuplicateCondition();
            for (int j = i + 1; j < localDecls.size(); j++) {
                T decl2 = localDecls.get(j);
                if (decl1Condition.value(decl2)) {
                    checkAndAddDuplicate(duplicates, decl1);
                    checkAndAddDuplicate(duplicates, decl2);
                }
            }
        }

        final List<T> otherDecls = new ArrayList<>();
        if (designs != null) {
            designs.forEach(design -> {
                if (!LSFDesignImpl.this.equals(design)) {
                    otherDecls.addAll(processor.process(design));
                }
                return true;
            });

            for (T decl1 : localDecls) {
                Condition<T> decl1Condition = decl1.getDuplicateCondition();
                for (T decl2 : otherDecls) {
                    if (decl1Condition.value(decl2)) {
                        checkAndAddDuplicate(duplicates, decl1);
                    }
                }
            }
        }

        return duplicates;
    }

    private <T extends LSFDesignElementDeclaration<T>> void checkAndAddDuplicate(Set<T> duplicates, T decl) {
        if (decl.getContainingFile().equals(getContainingFile())) {
            duplicates.add(decl);
        }
    }
}