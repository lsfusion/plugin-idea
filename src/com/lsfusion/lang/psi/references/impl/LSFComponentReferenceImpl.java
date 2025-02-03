package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.references.LSFComponentReference;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public abstract class LSFComponentReferenceImpl extends LSFDesignElementReferenceImpl<LSFComponentDeclaration> implements LSFComponentReference {
    protected LSFComponentReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public abstract LSFId getSimpleName();

    @Override
    protected Function<LSFDesign, Collection<LSFComponentDeclaration>> getElementsCollector() {
        return LSFDesign::getComponentDecls;
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFComponentDeclaration;
    }

    //    @Override
//    public LSFResolveResult resolveNoCache() {
//        String componentName = getSimpleName().getName();
//
//        List<LSFDeclaration> declarations = findComponents(componentName, resolveForm(this, true), getLSFFile());
//
//        LSFResolveResult.ErrorAnnotator errorAnnotator = null;
//        if (declarations.size() > 1) {
//            errorAnnotator = new LSFResolveResult.AmbigiousErrorAnnotator(this, declarations);
//        } else if (declarations.isEmpty()) {
//            errorAnnotator = new LSFResolveResult.NotFoundErrorAnnotator(this, declarations, false);
//        }
//
//        return new LSFResolveResult(declarations, errorAnnotator);
//    }
//
//    // it's hard to tell why components are resolved using index, but formElementReferences are resolved using processFormContext
//    // here LSFFormElementReferenceImpl.processDesignContext could / should be used (it is used in completeComponentUsage)
//    private static List<LSFDeclaration> findComponents(String componentName, LSFFormDeclaration formDeclaration, LSFFile file) {
//        Collection<LSFComponentStubDeclaration> stubDecls = ComponentIndex.getInstance().get(componentName, file.getProject(), file.getRequireScope());
//        List<LSFDeclaration> declarations = new ArrayList<>();
//
//        for (LSFComponentStubDeclaration stubDecl : stubDecls) {
//            if (formDeclaration == resolveForm(stubDecl.getComponentDecl(), false)) {
//                declarations.add(stubDecl.getComponentDecl());
//            }
//        }
//        return declarations;
//    }
//
//    private static LSFFormDeclaration resolveForm(PsiElement element, boolean exRef) {
//        LSFDesignStatement designStatement = PsiTreeUtil.getParentOfType(element, LSFDesignStatement.class);
//        if (designStatement != null) {
//            return designStatement.resolveFormDecl();
//        }
//        if(exRef) {
//            FormContext formContext = PsiTreeUtil.getParentOfType(element, FormContext.class);
//            if (formContext != null) {
//                return formContext.resolveFormDecl();
//            }
//        }
//        return null;
//    }
}
