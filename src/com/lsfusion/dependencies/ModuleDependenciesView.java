package com.lsfusion.dependencies;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.ide.impl.DataManagerImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.changes.committed.LabeledComboBoxAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.tools.SimpleActionGroup;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import com.jgraph.layout.organic.JGraphSelfOrganizingOrganicLayout;
import com.jgraph.layout.tree.JGraphCompactTreeLayout;
import com.jgraph.layout.tree.JGraphRadialTreeLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFModuleUsage;
import com.lsfusion.lang.psi.LSFRequireList;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.references.LSFReference;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ModuleDependenciesView extends JPanel implements Disposable {
    private final static String HIERARCHICAL_LAYOUT = "Hierarchical Layout";
    private final static String COMPACT_TREE_LAYOUT = "Compact Tree Layout";
    private final static String TREE_LAYOUT = "Tree Layout";
    private final static String SIMPLE_LAYOUT = "Simple Layout";
    private final static String ORGANIC_LAYOUT = "Organic Layout";
    private final static String FAST_ORGANIC_LAYOUT = "Fast Organic Layout";
    private final static String SELF_ORGANIZING_ORGANIC_LAYOUT = "Self Organizing Organic Layout";
    private final static String RADIAL_TREE_LAYOUT = "Radial Tree Layout";

    private final Project project;
    private ToolWindowEx toolWindow;

    private LSFModuleDeclaration currentModule;

    private boolean showRequired = true;
    private boolean showRequiring = false;
    private boolean allEdges = false;

    private boolean showDeclPath = false;
    private String latestTargetModule;

    private String currentLayout = COMPACT_TREE_LAYOUT;
    private JGraph jgraph;
    private ModuleGraphDataModel dataModel;
    private ListenableDirectedGraph g;
    private JGraphModelAdapter m_jgAdapter;
    private JBScrollPane scrollPane;

    private CheckboxAction showRequiredAction;
    private CheckboxAction showRequiringAction;
    private LabeledComboBoxAction layoutAction;

    public ModuleDependenciesView(Project project, final ToolWindowEx toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        setLayout(new BorderLayout());

        final TimerListener timerListener = new TimerListener() {
            @Override
            public ModalityState getModalityState() {
                return ModalityState.stateForComponent(toolWindow.getComponent());
            }

            @Override
            public void run() {
                if (toolWindow.isVisible()) {
                    checkUpdate();
                    if (showDeclPath) {
                        colorPath();
                    }
                }
            }
        };
        ActionManager.getInstance().addTimerListener(500, timerListener);

        ActionToolbar toolbar = createToolbar();

        toolbar.updateActionsImmediately();

        add(toolbar.getComponent(), BorderLayout.NORTH);

        layoutAction.setSelected(3);
        redraw();
    }

    private ActionToolbar createToolbar() {
        SimpleActionGroup actions = new SimpleActionGroup();

        actions.add(new AnAction(null, "Refresh", LSFIcons.Design.REFRESH) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                redrawCurrent();
            }
        });

        showRequiringAction = new CheckboxAction("Requiring") {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return showRequiring;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                showRequiring = state;
                if (!showRequired && !showRequiring) {
                    showRequiredAction.setSelected(e, true);
                } else {
                    redrawCurrent();
                }
            }
        };

        showRequiredAction = new CheckboxAction("Required") {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return showRequired;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                showRequired = state;
                if (!showRequired && !showRequiring) {
                    showRequiringAction.setSelected(e, true);
                } else {
                    redrawCurrent();
                }
            }
        };

        actions.add(showRequiredAction);
        actions.add(showRequiringAction);

        actions.add(new CheckboxAction("All edges") {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return allEdges;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                allEdges = state;
                redrawCurrent();
            }
        });

        layoutAction = new LabeledComboBoxAction("") {
            @Override
            protected void selectionChanged(Object selection) {
                if (!currentLayout.equals(selection)) {
                    currentLayout = (String) selection;
                    changeLayout(true);
                }
            }

            @Override
            protected ComboBoxModel createModel() {
                return new DefaultComboBoxModel(new Object[]{HIERARCHICAL_LAYOUT, SIMPLE_LAYOUT, TREE_LAYOUT, COMPACT_TREE_LAYOUT,
                        RADIAL_TREE_LAYOUT, ORGANIC_LAYOUT, FAST_ORGANIC_LAYOUT, SELF_ORGANIZING_ORGANIC_LAYOUT});
            }
        };
        actions.add(layoutAction);

        actions.add(new CheckboxAction("Show path to element") {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return showDeclPath;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                showDeclPath = state;
                if (!showDeclPath) {
                    latestTargetModule = null;
                    recolorGraph(null);
                } else {
                    colorPath();
                }
            }
        });

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actions, true);    
    }

    private void checkUpdate() {
        if (project.isDisposed()) return;

        final Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        final boolean insideToolwindow = SwingUtilities.isDescendingFrom(toolWindow.getComponent(), owner);
        if (insideToolwindow || JBPopupFactory.getInstance().isPopupActive()) {
            return;
        }

        final DataContext dataContext = DataManager.getInstance().getDataContext(owner);
        if (CommonDataKeys.PROJECT.getData(dataContext) != project) return;

        final VirtualFile[] files = hasFocus() ? null : CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);

        if (files != null && files.length == 1) {
            redraw();
        }
    }

    private void redraw() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        LSFModuleDeclaration moduleDeclaration = null;

        if (editor != null) {
            DataContext dataContext = new DataManagerImpl.MyDataContext(editor.getComponent());
            PsiElement targetElement = ConfigurationContext.getFromContext(dataContext).getPsiLocation();

            if (targetElement != null && targetElement.getContainingFile() instanceof LSFFile) {
                moduleDeclaration = ((LSFFile) targetElement.getContainingFile()).getModuleDeclaration();
            }
        }

        if (moduleDeclaration != null && moduleDeclaration != currentModule) {
            currentModule = moduleDeclaration;

            redrawCurrent();
        }
    }

    private void redrawCurrent() {
        Component comp = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (comp != null) {
            remove(comp);
        }

        dataModel = new ModuleGraphDataModel();

        if (showRequired) {
            createRequiredNode(dataModel, currentModule, new HashSet<LSFModuleDeclaration>());
        }

        if (showRequiring) {
            createRequiringNode(dataModel, currentModule, new HashSet<LSFModuleDeclaration>());
        }

        g = new ListenableDirectedGraph(DefaultEdge.class);

        fillGraph();

        m_jgAdapter = new JGraphModelAdapter(g);

        jgraph = new JGraph(m_jgAdapter);

        jgraph.setAntiAliased(true);
        jgraph.setEditable(false);
        jgraph.setDisconnectOnMove(false);
        jgraph.setAutoResizeGraph(true);

        if (!changeLayout(false)) {
            return;
        }

        scrollPane = new JBScrollPane(jgraph);
        add(scrollPane);

        initGraphDesign();

        recolorGraph(null);

        revalidate();
    }

    private boolean changeLayout(boolean update) {
        if (update && jgraph.getParent() == null) {
            redrawCurrent();
            return true;
        }

        JGraphLayout hir = null;
        if (currentLayout.equals(HIERARCHICAL_LAYOUT)) {
            hir = new JGraphHierarchicalLayout();
            ((JGraphHierarchicalLayout) hir).setOrientation(SwingConstants.WEST);
        } else if (currentLayout.equals(COMPACT_TREE_LAYOUT)) {
            hir = new JGraphCompactTreeLayout();
            ((JGraphCompactTreeLayout) hir).setPositionMultipleTrees(true);
            ((JGraphCompactTreeLayout) hir).setLevelDistance(80);
            ((JGraphCompactTreeLayout) hir).setRouteTreeEdges(true);
            ((JGraphCompactTreeLayout) hir).setOrientation(SwingConstants.WEST);
        } else if (currentLayout.equals(TREE_LAYOUT)) {
            hir = new JGraphTreeLayout();
            ((JGraphTreeLayout) hir).setPositionMultipleTrees(true);
            ((JGraphTreeLayout) hir).setRouteTreeEdges(true);
            ((JGraphTreeLayout) hir).setOrientation(SwingConstants.WEST);
        } else if (currentLayout.equals(SIMPLE_LAYOUT)) {
            hir = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE);
        } else if (currentLayout.equals(ORGANIC_LAYOUT)) {
            hir = new JGraphOrganicLayout();
        } else if (currentLayout.equals(FAST_ORGANIC_LAYOUT)) {
            hir = new JGraphFastOrganicLayout();
        } else if (currentLayout.equals(RADIAL_TREE_LAYOUT)) {
            hir = new JGraphRadialTreeLayout();
        } else if (currentLayout.equals(SELF_ORGANIZING_ORGANIC_LAYOUT)) {
            hir = new JGraphSelfOrganizingOrganicLayout();
        }

        try {
            final JGraphFacade graphFacade = new JGraphFacade(jgraph);
            assert hir != null;
            hir.run(graphFacade);
            final Map nestedMap = graphFacade.createNestedMap(true, true);
            jgraph.getGraphLayoutCache().edit(nestedMap);
            jgraph.getGraphLayoutCache().reload();

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        DefaultGraphCell vertexCell = m_jgAdapter.getVertexCell(dataModel.rootNode);
                        if (vertexCell != null) {
                            Rectangle bounds = jgraph.getCellBounds(vertexCell).getBounds();
                            scrollPane.getHorizontalScrollBar().setValue(Math.min(bounds.x - 50, scrollPane.getHorizontalScrollBar().getMaximum()));
                            scrollPane.getVerticalScrollBar().setValue(Math.min(bounds.y - 50, scrollPane.getVerticalScrollBar().getMaximum()));
                        }
                    } catch (IllegalArgumentException e) {
                        showUnableMessage();
                    }
                }
            });

        } catch (IllegalArgumentException e) {
            showUnableMessage();
            return false;
        }

        return true;
    }

    public void fillGraph() {
        for (ModuleGraphNode node : dataModel.getNodes()) {
            g.addVertex(node);
        }

        for (Pair<ModuleGraphNode, ModuleGraphNode> e : dataModel.getEdges()) {
            ModuleGraphEdge edge = new ModuleGraphEdge(e.first, e.second);
            g.addEdge(e.first, e.second, edge);
        }
    }

    public void initGraphDesign() {
        Map cellAttr = new HashMap();
        for (ModuleGraphNode node : dataModel.getNodes()) {
            DefaultGraphCell cell = m_jgAdapter.getVertexCell(node);
            if (cell != null) {
                Map attr = cell.getAttributes();
                GraphConstants.setAutoSize(attr, true);
                GraphConstants.setInset(attr, 3);

                cellAttr.put(cell, attr);
            }
        }

        for (Object gEdge : g.edgeSet()) {
            DefaultGraphCell cell = m_jgAdapter.getEdgeCell(gEdge);
            if (cell != null) {
                Map attr = cell.getAttributes();
                GraphConstants.setLabelEnabled(attr, false);

                cellAttr.put(cell, attr);
            }
        }

        m_jgAdapter.edit(cellAttr, null, null, null);
    }

    public void recolorGraph(String targetModule) {
        List path = null;
        if (targetModule != null) {
            path = dataModel.getPath(g, targetModule);
        }

        Map cellAttr = new HashMap();
        for (ModuleGraphNode node : dataModel.getNodes()) {
            DefaultGraphCell cell = m_jgAdapter.getVertexCell(node);
            if (cell != null) {
                Map attr = cell.getAttributes();
                Color background;

                if (node == dataModel.rootNode) {
                    background = new Color(255, 153, 0);
                } else if (targetModule != null && node == dataModel.getNode(targetModule)) {
                    background = new Color(114, 102, 255);
                } else if (node.required) {
                    background = new JBColor(new Color(0, 173, 57), JBColor.GREEN);
                } else {
                    background = new JBColor(new Color(48, 117, 255), JBColor.BLUE);
                }
                GraphConstants.setBackground(attr, background);

                cellAttr.put(cell, attr);
            }
        }

        for (Object gEdge : g.edgeSet()) {
            DefaultGraphCell cell = m_jgAdapter.getEdgeCell(gEdge);
            if (cell != null) {
                Map attr = cell.getAttributes();

                ModuleGraphEdge edge = (ModuleGraphEdge) gEdge;
                if (path != null && path.contains(edge)) {
                    GraphConstants.setLineColor(attr, new Color(114, 102, 255));
                    GraphConstants.setLineWidth(attr, 2);
                } else if ((edge.getSource()).required) {
                    GraphConstants.setLineColor(attr, new JBColor(new Color(0, 173, 57), JBColor.GREEN));
                    GraphConstants.setLineWidth(attr, 1);
                } else {
                    GraphConstants.setLineColor(attr, JBColor.BLUE);
                    GraphConstants.setLineWidth(attr, 1);
                }

                cellAttr.put(cell, attr);
            }
        }

        m_jgAdapter.edit(cellAttr, null, null, null);
    }

    private void colorPath() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        if (editor != null) {
            DataContext dataContext = new DataManagerImpl.MyDataContext(editor.getComponent());
            PsiElement targetElement = ConfigurationContext.getFromContext(dataContext).getPsiLocation();

            String declModuleName;
            if (targetElement != null && targetElement.getContainingFile() instanceof LSFFile) {
                LSFReference ref = PsiTreeUtil.getParentOfType(targetElement, LSFReference.class);
                if (ref != null) {
                    LSFDeclaration decl = ref.resolveDecl();
                    if (decl != null) {
                        declModuleName = decl.getLSFFile().getModuleDeclaration().getName();

                        if (declModuleName != null && !declModuleName.equals(latestTargetModule)) {
                            recolorGraph(declModuleName);
                            latestTargetModule = declModuleName;
                        }
                    }
                }
            }
        }
    }

    private void showUnableMessage() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(ModuleDependenciesView.this, "Unable to apply current layout", "Module dependencies", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void createRequiredNode(ModuleGraphDataModel dataModel, LSFModuleDeclaration module, Set<LSFModuleDeclaration> proceeded) {
        for (LSFModuleReference reference : module.getRequireRefs()) {
            LSFModuleDeclaration moduleDeclaration = reference.resolveDecl();
            if (moduleDeclaration != null && moduleDeclaration != module) {
                String declName = module.getDeclName();
                String moduleDeclarationName = moduleDeclaration.getName();
                if (allEdges || (!dataModel.containsNode(moduleDeclarationName) || !dataModel.containsNode(declName))) {
                    dataModel.createEdge(declName, moduleDeclarationName, true);

                    if (proceeded.add(moduleDeclaration)) {
                        createRequiredNode(dataModel, moduleDeclaration, proceeded);
                    }
                }
            }
        }
    }

    private void createRequiringNode(ModuleGraphDataModel dataModel, LSFModuleDeclaration module, Set<LSFModuleDeclaration> proceeded) {
        Set<PsiReference> refs = LSFGlobalResolver.getModuleReferences(module);

        for (PsiReference ref : refs) {
            if (ref instanceof LSFModuleUsage && PsiTreeUtil.getParentOfType((PsiElement) ref, LSFRequireList.class) != null) {
                LSFModuleDeclaration decl = PsiTreeUtil.getParentOfType((PsiElement) ref, LSFModuleDeclaration.class);
                if (decl != null) {
                    String declName = decl.getName();
                    String moduleName = module.getName();
                    if (allEdges || (!dataModel.containsNode(moduleName) || !dataModel.containsNode(declName))) {
                        dataModel.createEdge(declName, moduleName, false);
                    }

                    if (proceeded.add(decl)) {
                        createRequiringNode(dataModel, decl, proceeded);
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        //ignore
    }
}
