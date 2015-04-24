package com.lsfusion.dependencies.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.dependencies.DependenciesView;
import com.lsfusion.dependencies.GraphNode;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFModuleUsage;
import com.lsfusion.lang.psi.LSFRequireList;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFReference;

import java.util.Set;

public class ModuleDependenciesView extends DependenciesView {
    public ModuleDependenciesView(Project project, ToolWindowEx toolWindow) {
        super("Module dependencies", project, toolWindow);
    }

    @Override
    public boolean showPathToElement() {
        return true;
    }

    @Override
    public String getDependentTitle() {
        return "Requiring";
    }

    @Override
    public String getDependencyTitle() {
        return "Required";
    }

    @Override
    public int getAverageNodeWidth() {
        return 100;
    }

    @Override
    public LSFModuleDeclaration getSelectedElement() {
        PsiElement targetElement = getTargetEditorPsiElement();

        if (targetElement != null && targetElement.getContainingFile() instanceof LSFFile) {
            return((LSFFile) targetElement.getContainingFile()).getModuleDeclaration();
        }
        return null;
    }

    @Override
    public String getPathTarget() {
        PsiElement targetElement = getTargetEditorPsiElement();

        if (targetElement != null && targetElement.getContainingFile() instanceof LSFFile) {
            LSFReference ref = PsiTreeUtil.getParentOfType(targetElement, LSFReference.class);
            if (ref != null) {
                LSFDeclaration decl = ref.resolveDecl();
                if (decl != null) {
                    return decl.getLSFFile().getModuleDeclaration().getName();
                }
            }
        }
        return null;
    }

    public void createDependencyNode(PsiElement element, Set<PsiElement> proceeded) {
        LSFModuleDeclaration module = (LSFModuleDeclaration) element;
        
        for (LSFModuleDeclaration moduleDeclaration : module.getRequireModules()) {
            if (moduleDeclaration != null && moduleDeclaration != module) {
                String sourceDeclName = module.getDeclName();
                String targetDeclName = moduleDeclaration.getDeclName();
                if (allEdges || (!dataModel.containsNode(targetDeclName) || !dataModel.containsNode(sourceDeclName))) {
                    addModelEdge(module, moduleDeclaration, true);

                    if (proceeded.add(moduleDeclaration)) {
                        createDependencyNode(moduleDeclaration, proceeded);
                    }
                }
            }
        }
    }

    public void createDependentNode(PsiElement element, Set<PsiElement> proceeded) {
        LSFModuleDeclaration module = (LSFModuleDeclaration) element;
        
        Set<PsiReference> refs = LSFGlobalResolver.getModuleReferences(module);

        for (PsiReference ref : refs) {
            if (ref instanceof LSFModuleUsage && PsiTreeUtil.getParentOfType((PsiElement) ref, LSFRequireList.class) != null) {
                LSFModuleDeclaration decl = PsiTreeUtil.getParentOfType((PsiElement) ref, LSFModuleDeclaration.class);
                if (decl != null) {
                    String sourceDeclName = decl.getDeclName();
                    String targetDeclName = module.getDeclName();
                    if (allEdges || !dataModel.containsNode(targetDeclName) || !dataModel.containsNode(sourceDeclName)) {
                        addModelEdge(decl, module, false);
                    }

                    if (proceeded.add(decl)) {
                        createDependentNode(decl, proceeded);
                    }
                }
            }
        }
    }
    
    private void addModelEdge(LSFModuleDeclaration sourceModule, LSFModuleDeclaration targetModule, boolean required) {
        GraphNode sourceNode = dataModel.getNode(sourceModule.getDeclName());
        if (sourceNode == null) {
            sourceNode = new ModuleGraphNode(sourceModule, required);
        }
        GraphNode targetNode = dataModel.getNode(targetModule.getDeclName());
        if (targetNode == null) {
            targetNode = new ModuleGraphNode(targetModule, required);
        }

        dataModel.createEdge(sourceNode, targetNode, required);
    }
}
