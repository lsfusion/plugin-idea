package com.lsfusion.dependencies.property;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.lsfusion.dependencies.DependenciesView;
import com.lsfusion.dependencies.GraphNode;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.cache.PropertyDependenciesCache;
import com.lsfusion.lang.psi.cache.PropertyDependentsCache;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Set;
import java.util.function.Consumer;

public class PropertyDependenciesView extends DependenciesView {
    public PropertyDependenciesView(Project project, ToolWindowEx toolWindow) {
        super("Property dependencies", project, toolWindow, false);
    }

    @Override
    public void createDependencyNode(PsiElement element, Set<PsiElement> proceeded) {
        LSFActionOrGlobalPropDeclaration<?, ?> sourceProperty = (LSFActionOrGlobalPropDeclaration<?, ?>) element;

        if (sourceProperty != null && !sourceProperty.isAction()) {
            Set<LSFActionOrGlobalPropDeclaration<?, ?>> targetProperties = DumbService.getInstance(project).runReadActionInSmartMode(
                    () -> PropertyDependenciesCache.getInstance(project).resolveWithCaching(sourceProperty)
            );
            if (targetProperties != null) {
                for (LSFActionOrGlobalPropDeclaration<?, ?> targetProperty : targetProperties) {
                    if (allEdges || (!dataModel.containsNode(sourceProperty.getPresentableText()) || !dataModel.containsNode(targetProperty.getPresentableText()))) {
                        addModelEdge(sourceProperty, targetProperty, true);
                    }

                    if (proceeded.add(targetProperty)) {
                        createDependencyNode(targetProperty, proceeded);
                    }
                }
            }
        }
    }

    @Override
    public void createDependentNode(GlobalSearchScope scope, PsiElement element, Set<PsiElement> proceeded) {
        LSFActionOrGlobalPropDeclaration<?, ?> targetProperty = (LSFActionOrGlobalPropDeclaration<?, ?>) element;

        if (targetProperty != null) {
            Set<LSFActionOrGlobalPropDeclaration<?, ?>> sourceProperties = DumbService.getInstance(project).runReadActionInSmartMode(
                    () -> PropertyDependentsCache.getInstance(project).resolveWithCaching(targetProperty)
            );
            if (sourceProperties != null) {
                for (LSFActionOrGlobalPropDeclaration<?, ?> sourceProperty : sourceProperties) {
                    if (sourceProperty != null && !sourceProperty.isAction() && (scope == null || scope.accept(sourceProperty.getLSFFile().getVirtualFile()))) {
                        if (allEdges || !dataModel.containsNode(targetProperty.getPresentableText()) || !dataModel.containsNode(sourceProperty.getPresentableText())) {
                            addModelEdge(sourceProperty, targetProperty, false);
                        }

                        if (proceeded.add(sourceProperty)) {
                            createDependentNode(scope, sourceProperty, proceeded);
                        }
                    }
                }
            }
        }
    }

    private void addModelEdge(LSFActionOrGlobalPropDeclaration<?, ?> sourceProperty, LSFActionOrGlobalPropDeclaration<?, ?> targetProperty, boolean dependency) {
        GraphNode sourceNode = dataModel.getNode(sourceProperty.getPresentableText());
        if (sourceNode == null) {
            sourceNode = new PropertyGraphNode(sourceProperty, dependency);
        }
        GraphNode targetNode = dataModel.getNode(targetProperty.getPresentableText());
        if (targetNode == null) {
            targetNode = new PropertyGraphNode(targetProperty, dependency);
        }

        dataModel.createEdge(sourceNode, targetNode, dependency);
    }

    @Override
    public void getSelectedElement(Consumer<PsiElement> elementConsumer) {
        getTargetEditorPsiElement(targetElement -> {
            PsiElement element = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
                if (targetElement != null && PsiTreeUtil.getParentOfType(targetElement, LSFId.class) != null) {
                    PsiElement parent = PsiTreeUtil.getParentOfType(targetElement, LSFActionOrPropReference.class, LSFActionOrGlobalPropDeclaration.class);
                    if (parent != null) {
                        if (parent instanceof LSFActionOrPropReference) {
                            return((LSFActionOrPropReference<?, ?>) parent).resolveDecl();
                        } else {
                            return parent;
                        }
                    }
                }
                return null;
            });
            elementConsumer.accept(element);
        }, false);
    }

    @Override
    public boolean showPathToElement() {
        return false;
    }
    
    @Override
    public void getPathTarget(Consumer<String> pathConsumer) {
        pathConsumer.accept(null);
    }

    @Override
    public int getAverageNodeWidth() {
        return 400;
    }

    @Override
    public Color getDependentNodeColor(GraphNode node) {
        Color special = getSpecificNodeColor(node);
        return special != null ? special : super.getDependentNodeColor(node);
    }

    @Override
    public Color getDependencyNodeColor(GraphNode node) {
        Color special = getSpecificNodeColor(node);
        return special != null ? special : super.getDependencyNodeColor(node);
    }
    
    private Color getSpecificNodeColor(GraphNode node) {
        LSFActionOrGlobalPropDeclaration<?, ?> prop = ((PropertyGraphNode) node).property;
        if (prop instanceof LSFGlobalPropDeclaration) {
            LSFGlobalPropDeclaration<?, ?> property = (LSFGlobalPropDeclaration<?, ?>) prop;
            if (property.isAbstract()) {
                return new JBColor(Gray._139, Gray._139);
            } else if (property.isDataProperty()) {
                return new JBColor(new Color(181, 172, 255), new Color(181, 172, 255));
            }
        }
        return null;
    }

    @Override
    public Border getNodeBorder(GraphNode node) {
        LSFActionOrGlobalPropDeclaration<?, ?> prop = ((PropertyGraphNode) node).property;
        if (prop instanceof LSFGlobalPropDeclaration && ((LSFGlobalPropDeclaration<?, ?>) prop).isMaterializedProperty()) {
            return new LineBorder(new JBColor(new Color(156, 121, 255), new Color(156, 121, 255)), 2);
        }
        return super.getNodeBorder(node);
    }
}
