package com.lsfusion.dependencies.module;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.dependencies.DependenciesView;
import com.lsfusion.dependencies.GraphNode;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.cache.ModuleDependentsCache;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ModuleDependenciesView extends DependenciesView {
    public ModuleDependenciesView(Project project, ToolWindowEx toolWindow) {
        super("Module dependencies", project, toolWindow, true);
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
    public void getSelectedElement(Consumer<PsiElement> elementConsumer) {
        getTargetEditorPsiElement(targetElement -> {
            if (targetElement != null) {
                LSFModuleDeclaration moduleDeclaration = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
                    PsiFile containingFile = targetElement.getContainingFile();
                    if (containingFile instanceof LSFFile) {
                        return ((LSFFile) containingFile).getModuleDeclaration();
                    }
                    return null;
                });
                elementConsumer.accept(moduleDeclaration);
            }
            elementConsumer.accept(null);
        });
    }

    @Override
    public void getPathTarget(Consumer<String> pathConsumer) {
        getTargetEditorPsiElement(targetElement -> {
            String module = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
                if (targetElement != null && targetElement.getContainingFile() instanceof LSFFile) {
                    LSFReference<?> ref = PsiTreeUtil.getParentOfType(targetElement, LSFReference.class);
                    if (ref != null) {
                        LSFDeclaration decl = ref.resolveDecl();
                        if (decl != null) {
                            return decl.getLSFFile().getModuleDeclaration().getName();
                        }
                    }
                }
                return null;
            });
            pathConsumer.accept(module);
        });
    }

    @Override
    public void createDependencyNode(PsiElement element, Set<PsiElement> proceeded) {
        if(element != null) {
            LSFModuleDeclaration module = (LSFModuleDeclaration) element;

            if(onlyLeafs) {
                List<LSFModuleDeclaration> leafModules = new ArrayList<>();
                findDependencyLeafModules(module, leafModules, new HashSet<>());
                for(LSFModuleDeclaration leafModule : leafModules) {
                    addModelEdge(module, leafModule, true);
                }
            } else {
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
        }
    }

    private void findDependencyLeafModules(LSFModuleDeclaration module, List<LSFModuleDeclaration> leafModules, Set<LSFModuleDeclaration> seenModules) {
        seenModules.add(module);
        List<LSFModuleDeclaration> requireModules = module.getRequireModules();
        if (seenModules.containsAll(requireModules)) {
            leafModules.add(module);
        } else {
            for (LSFModuleDeclaration requiredModule : requireModules) {
                findDependencyLeafModules(requiredModule, leafModules, seenModules);
            }
        }
    }

    @Override
    public void createDependentNode(GlobalSearchScope scope, PsiElement element, Set<PsiElement> proceeded) {
        if(element != null) {
            LSFModuleDeclaration module = (LSFModuleDeclaration) element;

            if(onlyLeafs) {
                List<LSFModuleDeclaration> leafModules = new ArrayList<>();
                findDependentLeafModules(module, leafModules, new HashSet<>());
                for (LSFModuleDeclaration leafModule : leafModules) {
                    if (scope == null || scope.accept(leafModule.getLSFFile().getVirtualFile())) {
                        addModelEdge(leafModule, module, false);
                    }
                }
            } else {
                Set<LSFModuleDeclaration> refModules = DumbService.getInstance(project).runReadActionInSmartMode(
                        () -> ModuleDependentsCache.getInstance(project).resolveWithCaching(module)
                );
                if (refModules != null) {
                    for (LSFModuleDeclaration decl : refModules) {
                        assert decl != null;
                        if (scope == null || scope.accept(decl.getLSFFile().getVirtualFile())) {
                            String sourceDeclName = decl.getDeclName();
                            String targetDeclName = module.getDeclName();
                            if (allEdges || !dataModel.containsNode(targetDeclName) || !dataModel.containsNode(sourceDeclName)) {
                                addModelEdge(decl, module, false);
                            }

                            if (proceeded.add(decl)) {
                                createDependentNode(scope, decl, proceeded);
                            }
                        }
                    }
                }
            }
        }
    }

    private void findDependentLeafModules(LSFModuleDeclaration module, List<LSFModuleDeclaration> leafModules, Set<LSFModuleDeclaration> seenModules) {
        seenModules.add(module);
        Set<LSFModuleDeclaration> refModules = ModuleDependentsCache.getInstance(project).resolveWithCaching(module);
        if(refModules != null) {
            if (seenModules.containsAll(refModules)) {
                leafModules.add(module);
            } else {
                for (LSFModuleDeclaration refModule : refModules) {
                    findDependentLeafModules(refModule, leafModules, seenModules);
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
