package com.lsfusion.hierarchy.usages;

import com.intellij.icons.AllIcons;
import com.intellij.ide.hierarchy.*;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.SourceComparator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.references.LSFReference;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class LSFUsageHierarchyBrowser extends CallHierarchyBrowserBase {
    private static final Logger LOG = Logger.getInstance("#com.lsfusion.hierarchy.usages.LSFUsageHierarchyBrowser");

    private boolean sortByType = true;
    private UsageTypeComparator usageTypeComparatorWithAlpha = new UsageTypeComparator(true);
    private UsageTypeComparator usageTypeComparatorWithoutAlpha = new UsageTypeComparator(false);

    public LSFUsageHierarchyBrowser(@NotNull Project project, @NotNull PsiElement target) {
        super(project, target);
    }

    @Override
    protected PsiElement getElementFromDescriptor(@NotNull HierarchyNodeDescriptor descriptor) {
        if (descriptor instanceof LSFUsageHierarchyNodeDescriptor) {
            LSFUsageHierarchyNodeDescriptor nodeDescriptor = (LSFUsageHierarchyNodeDescriptor) descriptor;
            return nodeDescriptor.getPsiElement();
        }
        return null;
    }

    @Override
    protected void prependActions(@NotNull DefaultActionGroup actionGroup) {
        actionGroup.add(new ToggleAction("Sort by Usage Type", "Sort by Usage Type", AllIcons.General.Filter) {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return sortByType;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                sortByType = state;

                final Comparator<NodeDescriptor> comparator = getComparator();
                final Collection<HierarchyTreeBuilder> builders = myBuilders.values();
                for (final HierarchyTreeBuilder builder : builders) {
                    builder.setNodeDescriptorComparator(comparator);
                }
            }
        });
        actionGroup.add(new AlphaSortAction());
    }

    @Override
    protected PsiElement getOpenFileElementFromDescriptor(@NotNull HierarchyNodeDescriptor descriptor) {
        if (descriptor instanceof LSFUsageHierarchyNodeDescriptor) {
            LSFUsageHierarchyNodeDescriptor nodeDescriptor = (LSFUsageHierarchyNodeDescriptor) descriptor;
            return nodeDescriptor.getPsiElement();
        }
        return null;
    }

    @Override
    protected void createTrees(@NotNull Map<String, JTree> trees) {
        trees.put(CALLER_TYPE, createTree(false));
    }

    @Override
    protected boolean isApplicableElement(@NotNull PsiElement element) {
        return element instanceof LSFId && !(element instanceof LSFBuiltInClassName);
    }

    @Nullable
    @Override
    protected HierarchyTreeStructure createHierarchyTreeStructure(@NotNull String type, @NotNull PsiElement psiElement) {
        if (CALLER_TYPE.equals(type)) {
            return new LSFUsageHierarchyTreeStructure(myProject, findSourceStatement(psiElement));
        } else {
            LOG.error("unexpected type: " + type);
            return null;
        }
    }

    public static PsiElement findSourceStatement(PsiElement target) {
        PsiElement decl = null;
        PsiElement parent = target.getParent();
        while (parent != null) {
            if (parent instanceof LSFReference) {
                decl = ((LSFReference) parent).resolveDecl();
                break;
            } else if (parent instanceof LSFDeclaration) {
                decl = parent;
                break;
            }

            parent = parent.getParent();
        }
        return LSFPsiUtils.getStatementParent(decl);
    }

    @Nullable
    @Override
    protected Comparator<NodeDescriptor> getComparator() {
        if (sortByType) {
            return HierarchyBrowserManager.getInstance(myProject).getState().SORT_ALPHABETICALLY ? usageTypeComparatorWithAlpha : usageTypeComparatorWithoutAlpha;
        }
        if (HierarchyBrowserManager.getInstance(myProject).getState().SORT_ALPHABETICALLY) {
            return AlphaComparator.INSTANCE;
        }
        return SourceComparator.INSTANCE;
    }

    private class UsageTypeComparator implements Comparator<NodeDescriptor> {
        private boolean withAlpha;

        private final List<Class> classOrder = new ArrayList<Class>(Arrays.asList(LSFClassDeclaration.class, LSFExplicitInterfacePropertyStatement.class,
                LSFOverrideStatement.class, LSFEventStatement.class, LSFGlobalEventStatement.class, LSFFollowsStatement.class,
                LSFWriteWhenStatement.class, LSFAspectStatement.class, LSFShowDepStatement.class, LSFConstraintStatement.class,
                LSFFormStatement.class, LSFDesignStatement.class, LSFNavigatorStatement.class, LSFTableStatement.class,
                LSFLoggableStatement.class, LSFIndexStatement.class));

        public UsageTypeComparator(boolean withAlpha) {
            this.withAlpha = withAlpha;
        }

        @Override
        public int compare(NodeDescriptor o1, NodeDescriptor o2) {
            if (o1 != null && o2 != null) {
                LSFUsageHierarchyNodeDescriptor q1 = (LSFUsageHierarchyNodeDescriptor) o1;
                LSFUsageHierarchyNodeDescriptor q2 = (LSFUsageHierarchyNodeDescriptor) o2;

                int result = getIndex(q1) - getIndex(q2);
                if (result == 0) {
                    result = compareEqual(q1, q2);
                }
                if (result == 0) {
                    if (withAlpha) {
                        return AlphaComparator.INSTANCE.compare(q1, q2);
                    } else {
                        return SourceComparator.INSTANCE.compare(q1, q2);
                    }
                }
                return result;
            }
            return 0;
        }

        private int compareEqual(LSFUsageHierarchyNodeDescriptor d1, LSFUsageHierarchyNodeDescriptor d2) {
            PsiElement nodeElement1 = d1.getPsiElement();
            PsiElement nodeElement2 = d2.getPsiElement();
            if (nodeElement1 instanceof LSFExplicitInterfacePropertyStatement && nodeElement2 instanceof LSFExplicitInterfacePropertyStatement) {
                boolean isAction1 = ((LSFExplicitInterfacePropertyStatement) nodeElement1).getExplicitValuePropertyStatement().getPropertyStatement().isAction();
                boolean isAction2 = ((LSFExplicitInterfacePropertyStatement) nodeElement2).getExplicitValuePropertyStatement().getPropertyStatement().isAction();

                if (isAction1) {
                    if (!isAction2) {
                        return 1;
                    }
                } else {
                    if (isAction2) {
                        return -1;
                    }
                }
            }
            return 0;
        }

        private int getIndex(LSFUsageHierarchyNodeDescriptor node) {
            for (Class ord : classOrder) {
                if (ord.isInstance(node.getPsiElement())) {
                    return classOrder.indexOf(ord);
                }
            }
            return 2;
        }
    }
}
