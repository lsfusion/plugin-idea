package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.design.FormView;
import com.lsfusion.design.GroupObjectContainerSet;
import com.lsfusion.design.TreeGroupContainerSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFComponentStubDeclaration;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFComponentReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.indexes.ComponentIndex;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class LSFComponentReferenceImpl extends LSFReferenceImpl<LSFDeclaration> implements LSFComponentReference {
    protected LSFComponentReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFId getSimpleName() {
        return getMultiCompoundID();
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        String componentName = getSimpleName().getName();
        LSFFormDeclaration formDeclaration = resolveForm(this);

        Collection<LSFComponentStubDeclaration> stubDecls = ComponentIndex.getInstance().get(componentName, getProject(), getLSFFile().getRequireScope());
        List<LSFDeclaration> declarations = new ArrayList<LSFDeclaration>();

        for (LSFComponentStubDeclaration stubDecl : stubDecls) {
            if (formDeclaration == resolveForm(stubDecl.getComponentDecl())) {
                declarations.add(stubDecl.getComponentDecl());
            }
        }

        if (declarations.isEmpty() && formDeclaration != null) {
            Collection<LSFFormExtend> formExtends = LSFGlobalResolver.findExtendElements(formDeclaration, LSFStubElementTypes.EXTENDFORM, getLSFFile()).findAll();
            for (LSFFormExtend formExtend : formExtends) {
                LSFDeclaration declaration = getDefaultContainers(formExtend).get(componentName);
                if (declaration != null) {
                    declarations.add(declaration);
                }
            }
        }

        LSFResolveResult.ErrorAnnotator errorAnnotator = null;
        if (declarations.size() > 1) {
            final Collection<? extends LSFDeclaration> finalDeclarations = declarations;
            errorAnnotator = new LSFResolveResult.ErrorAnnotator() {
                @Override
                public Annotation resolveErrorAnnotation(AnnotationHolder holder) {
                    return resolveAmbiguousErrorAnnotation(holder, finalDeclarations);
                }
            };
        }

        return new LSFResolveResult(declarations, errorAnnotator);
    }

    private LSFFormDeclaration resolveForm(PsiElement element) {
        LSFDesignStatement designStatement = PsiTreeUtil.getParentOfType(element, LSFDesignStatement.class);
        if (designStatement != null) {
            return designStatement.resolveFormDecl();
        }
        return null;
    }

    private Map<String, LSFDeclaration> getDefaultContainers(LSFFormExtend formExtend) {
        Map<String, LSFDeclaration> result = new HashMap<String, LSFDeclaration>();
        for (LSFFormTreeGroupObjectList lsfFormTreeGroupObjectList : PsiTreeUtil.findChildrenOfType(formExtend, LSFFormTreeGroupObjectList.class)) {
            LSFTreeGroupDeclaration treeGroupDeclaration = lsfFormTreeGroupObjectList.getTreeGroupDeclaration();
            if (treeGroupDeclaration != null) {
                String tgoName = treeGroupDeclaration.getSimpleName().getText();
                if (tgoName != null) {
                    result.put(FormView.getTreeSID(tgoName) + TreeGroupContainerSet.TREE_GROUP_CONTAINER, treeGroupDeclaration);
                    result.put(FormView.getToolbarSID(tgoName), treeGroupDeclaration);
                    result.put(FormView.getFilterSID(tgoName), treeGroupDeclaration);
                }
            }
        }

        for (LSFGroupObjectDeclaration lsfGroupObjectDeclaration : formExtend.getGroupObjectDecls()) {
            String goName = lsfGroupObjectDeclaration.getDeclName();
            if (goName != null) {
                result.put(goName + GroupObjectContainerSet.CONTROLS_CONTAINER, lsfGroupObjectDeclaration);
                result.put(goName + GroupObjectContainerSet.CONTROLS_RIGHT_CONTAINER, lsfGroupObjectDeclaration);
                result.put(goName + GroupObjectContainerSet.FILTERS_CONTAINER, lsfGroupObjectDeclaration);
                result.put(goName + GroupObjectContainerSet.GRID_CONTAINER, lsfGroupObjectDeclaration);
                result.put(goName + GroupObjectContainerSet.GROUP_CONTAINER, lsfGroupObjectDeclaration);
                result.put(goName + GroupObjectContainerSet.PANEL_CONTAINER, lsfGroupObjectDeclaration);
                result.put(goName + GroupObjectContainerSet.PANEL_PROPS_CONTAINER, lsfGroupObjectDeclaration);
                result.put(goName + GroupObjectContainerSet.TOOLBAR_PROPS_CONTAINER, lsfGroupObjectDeclaration);
                result.put(FormView.getGridSID(goName), lsfGroupObjectDeclaration);
                result.put(FormView.getToolbarSID(goName), lsfGroupObjectDeclaration);
                result.put(FormView.getFilterSID(goName), lsfGroupObjectDeclaration);
                result.put(FormView.getShowTypeSID(goName), lsfGroupObjectDeclaration);
                result.put(FormView.getClassChooserSID(goName), lsfGroupObjectDeclaration);

                for (LSFFormObjectDeclaration lsfFormObjectDeclaration : lsfGroupObjectDeclaration.findObjectDecarations()) {
                    LSFSimpleName simpleName = lsfFormObjectDeclaration.getSimpleName();
                    if (simpleName != null) {
                        String name = simpleName.getName();
                        if (name != null) {
                            result.put(FormView.getClassChooserSID(name), lsfFormObjectDeclaration);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
