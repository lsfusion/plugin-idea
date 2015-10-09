package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.design.FormView;
import com.lsfusion.design.GroupObjectContainerSet;
import com.lsfusion.design.TreeGroupContainerSet;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.indexes.ComponentIndex;
import com.lsfusion.lang.psi.indexes.GroupIndex;
import com.lsfusion.lang.psi.references.LSFComponentReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
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

        if (declarations.isEmpty()) {
            // список групп свойств
            Set<String> groups = new HashSet<String>();
            for (String key : GroupIndex.getInstance().getAllKeys(getProject())) {
                Collection<LSFGroupDeclaration> groupDeclarations = GroupIndex.getInstance().get(key, getProject(), getLSFFile().getRequireScope());
                for (LSFGroupDeclaration groupDeclaration : groupDeclarations) {
                    groups.add(groupDeclaration.getNameIdentifier().getName());
                }
            }
            
            // стандартные контейнеры групп объектов
            if (formDeclaration != null) {
                Collection<LSFFormExtend> formExtends = LSFGlobalResolver.findExtendElements(formDeclaration, LSFStubElementTypes.EXTENDFORM, getLSFFile()).findAll();
                for (LSFFormExtend formExtend : formExtends) {
                    LSFDeclaration declaration = getDefaultContainers(formExtend, groups).get(componentName);
                    if (declaration != null) {
                        declarations.add(declaration);
                    }
                }
            }
            
            // стандартные контейнеры формы
            LSFDeclaration builtInFormComponent = getBuiltInFormComponents().get(componentName);
            if (builtInFormComponent != null) {
                declarations.add(builtInFormComponent);
            }

            // стандартные контейнеры свойсв без группы
            LSFDeclaration noGroupDeclaration = getNoGroupDeclarations(groups).get(componentName);
            if (noGroupDeclaration != null) {
                declarations.add(noGroupDeclaration);
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

    private LSFFormDeclaration resolveForm(PsiElement element) {
        LSFDesignStatement designStatement = PsiTreeUtil.getParentOfType(element, LSFDesignStatement.class);
        if (designStatement != null) {
            return designStatement.resolveFormDecl();
        }
        return null;
    }

    private Map<String, LSFDeclaration> getDefaultContainers(LSFFormExtend formExtend, Set<String> groups) {
        Map<String, LSFDeclaration> result = new HashMap<String, LSFDeclaration>();
        
        // стандартные контейнеры дерева объектов
        for (LSFFormTreeGroupObjectList lsfFormTreeGroupObjectList : PsiTreeUtil.findChildrenOfType(formExtend, LSFFormTreeGroupObjectList.class)) {
            LSFTreeGroupDeclaration treeGroupDeclaration = lsfFormTreeGroupObjectList.getTreeGroupDeclaration();
            if (treeGroupDeclaration != null) {
                String tgoName = treeGroupDeclaration.getSimpleName().getText();
                if (tgoName != null) {
                    String treeSID = FormView.getTreeSID(tgoName);
                    result.put(treeSID, treeGroupDeclaration);
                    result.put(treeSID + TreeGroupContainerSet.TREE_GROUP_CONTAINER, treeGroupDeclaration);
                    result.put(treeSID + GroupObjectContainerSet.CONTROLS_CONTAINER, treeGroupDeclaration);
                    result.put(treeSID + GroupObjectContainerSet.CONTROLS_RIGHT_CONTAINER, treeGroupDeclaration);
                    result.put(treeSID + GroupObjectContainerSet.FILTERS_CONTAINER, treeGroupDeclaration);
                    result.put(treeSID + GroupObjectContainerSet.TOOLBAR_PROPS_CONTAINER, treeGroupDeclaration);
                    result.put(FormView.getToolbarSID(treeSID), treeGroupDeclaration);
                    result.put(FormView.getFilterSID(treeSID), treeGroupDeclaration);
                }
            }
        }

        // стандартные контейнеры групп объектов
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
            
            // контейнеры групп свойств
            for (String group : groups) {
                result.put(goName + "." + group, lsfGroupObjectDeclaration);    
            }
        }

        // контенеры групп фильтров
        for (LSFFilterGroupDeclaration filterGroupDeclaration : formExtend.getFilterGroupDecls()) {
            result.put(FormView.getRegularFilterGroupSID(filterGroupDeclaration.getDeclName()), filterGroupDeclaration);    
        }
        
        return result;
    }
    
    private Map<String, LSFDeclaration> getBuiltInFormComponents() {
        Map<String, LSFDeclaration> result = new HashMap<String, LSFDeclaration>();
        for (LSFComponentDeclaration componentDeclaration : LSFElementGenerator.getBuiltInFormComponents(getProject())) {
            result.put(componentDeclaration.getName(), componentDeclaration);
        }    
        return result;
    }
        
    private Map<String, LSFDeclaration> getNoGroupDeclarations(Set<String> groups) {
        List<String> nogroupGroupContainers = new ArrayList<String>();
        for (String group : groups) {
            nogroupGroupContainers.add("NOGROUP." + group);
        }
        Map<String, LSFDeclaration> result = new HashMap<String, LSFDeclaration>();
        for (LSFComponentDeclaration componentDeclaration : LSFElementGenerator.createFormComponents(getProject(), nogroupGroupContainers)) {
            result.put(componentDeclaration.getName(), componentDeclaration);
        }    
        return result;
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
