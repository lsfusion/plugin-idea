package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.design.DefaultFormView;
import com.lsfusion.design.FormView;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.indexes.ComponentIndex;
import com.lsfusion.lang.psi.indexes.GroupIndex;
import com.lsfusion.lang.psi.references.LSFComponentReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class LSFComponentReferenceImpl extends LSFReferenceImpl<LSFDeclaration> implements LSFComponentReference {
    protected LSFComponentReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public abstract LSFId getSimpleName();

    @Override
    public LSFResolveResult resolveNoCache() {
        String componentName = getSimpleName().getName();
        LSFFormDeclaration formDeclaration = resolveForm(this, true);

        Collection<LSFComponentStubDeclaration> stubDecls = ComponentIndex.getInstance().get(componentName, getProject(), getLSFFile().getRequireScope());
        List<LSFDeclaration> declarations = new ArrayList<>();

        for (LSFComponentStubDeclaration stubDecl : stubDecls) {
            if (formDeclaration == resolveForm(stubDecl.getComponentDecl(), false)) {
                declarations.add(stubDecl.getComponentDecl());
            }
        }
        if (declarations.isEmpty()) {
            // стандартные контейнеры формы
            LSFDeclaration builtInFormComponent = getBuiltInFormComponents().get(componentName);
            if (builtInFormComponent != null) {
                declarations.add(builtInFormComponent);
            }
        }

        LSFResolveResult.ErrorAnnotator errorAnnotator = null;
        if (declarations.size() > 1) {
            errorAnnotator = new LSFResolveResult.AmbigiousErrorAnnotator(this, declarations);
        } else if (declarations.isEmpty()) {
            errorAnnotator = new LSFResolveResult.NotFoundErrorAnnotator(this, declarations);
        }

        return new LSFResolveResult(declarations, errorAnnotator);
    }

    private Map<String, LSFDeclaration> getBuiltInFormComponents() {
        Map<String, LSFDeclaration> result = new HashMap<>();
        for (LSFComponentDeclaration componentDeclaration : LSFElementGenerator.getBuiltInFormComponents(getProject())) {
            result.put(componentDeclaration.getName(), componentDeclaration);
        }
        return result;
    }

    private LSFFormDeclaration resolveForm(PsiElement element, boolean exRef) {
        LSFDesignStatement designStatement = PsiTreeUtil.getParentOfType(element, LSFDesignStatement.class);
        if (designStatement != null) {
            return designStatement.resolveFormDecl();
        }
        if(exRef) {
            FormContext formContext = PsiTreeUtil.getParentOfType(element, FormContext.class);
            if (formContext != null) {
                return formContext.resolveFormDecl();
            }
        }
        return null;
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
