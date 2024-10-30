package com.lsfusion.dependencies;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.tools.SimpleActionGroup;
import com.intellij.ui.DarculaColors;
import com.intellij.ui.JBColor;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
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
import com.lsfusion.dependencies.module.DependencySpeedSearch;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.FlexPanel;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFFileUtils;
import org.jdesktop.swingx.VerticalLayout;
import org.jetbrains.annotations.NotNull;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lsfusion.dependencies.GraphEdgesComboAction.ALL_EDGES;
import static com.lsfusion.dependencies.GraphEdgesComboAction.ONLY_LEAFS;
import static com.lsfusion.dependencies.GraphLayoutComboAction.*;

public abstract class DependenciesView extends JPanel implements Disposable {
    protected String title;

    protected final Project project;
    protected ToolWindowEx toolWindow;
    private boolean showTargetField;

    protected PsiElement currentElement;

    protected String latestTargetElementInPath;

    protected GraphDataModel dataModel;
    
    protected JGraph jgraph;
    protected ListenableDirectedGraph g;
    protected JGraphModelAdapter m_jgAdapter;
    protected JBScrollPane scrollPane;
    
    protected GraphNode selectedInSearch;

    //first toolbar
    private BGTCheckboxAction showRequiredAction;
    private boolean showRequired = true;
    private BGTCheckboxAction showRequiringAction;
    private boolean showRequiring = false;
    private GraphEdgesComboAction edgesAction;
    protected boolean allEdges = false;
    protected boolean onlyLeafs = false;
    private GraphLayoutComboAction layoutAction;
    private boolean showDeclPath = false;

    //second toolbar
    private ModuleComboAction moduleAction;
    private SearchTextField target;

    //third toolbar
    private boolean manualMode = false;

    protected double latestScale = 1;

    public DependenciesView(String title, Project project, final ToolWindowEx toolWindow, boolean showTargetField) {
        this.title = title;
        this.project = project;
        this.toolWindow = toolWindow;
        this.showTargetField = showTargetField;

        setLayout(new BorderLayout());

        final TimerListener timerListener = new TimerListener() {
            @Override
            public ModalityState getModalityState() {
                return ModalityState.stateForComponent(toolWindow.getComponent());
            }

            @Override
            public void run() {
                if (toolWindow.isVisible() && isVisible()) {
                    new Task.Backgroundable(project, "Updating diagram") {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            checkUpdate();
                            if (showPathToElement() && showDeclPath) {
                                findAndColorPath();
                            }
                        }
                    }.queue();
                }
            }
        };
        ActionManager.getInstance().addTimerListener(timerListener);
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!toolWindow.isVisible() && jgraph != null && jgraph.getOffscreen() != null && jgraph.getOffgraphics() != null) {
                    jgraph.releaseOffscreenResources();
                }
            }
        }, 0, 5 * 60 * 1000);

        JPanel toolbar = new JPanel(new VerticalLayout());
        toolbar.add(createFirstToolbar().getComponent());
        toolbar.add(createSecondToolbar());
        toolbar.add(createThirdToolbar().getComponent());

        add(toolbar, BorderLayout.NORTH);
    }

    private ActionToolbar createFirstToolbar() {
        SimpleActionGroup actions = new SimpleActionGroup();

        actions.add(showRequiredAction = new BGTCheckboxAction(getDependencyTitle()) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return showRequired;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                showRequired = state;
                if (!showRequired && !showRequiring) {
                    showRequiringAction.setSelected(e, true);
                }
            }
        });

        actions.add(showRequiringAction = new BGTCheckboxAction(getDependentTitle()) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return showRequiring;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                showRequiring = state;
                if (!showRequired && !showRequiring) {
                    showRequiredAction.setSelected(e, true);
                }
            }
        });

        actions.add(edgesAction = new GraphEdgesComboAction("Edges:") {
            @Override
            protected void changeEdges() {
                DependenciesView.this.changeEdges();
            }
        });

        actions.add(layoutAction = new GraphLayoutComboAction("Layout:") {
            @Override
            protected void changeLayout(boolean update) {
                if (!manualMode) {
                    DependenciesView.this.changeLayout(update);
                }
            }
        });

        if (showPathToElement()) {
            actions.add(new BGTCheckboxAction("Path to element") {
                @Override
                public boolean isSelected(@NotNull AnActionEvent e) {
                    return showDeclPath;
                }

                @Override
                public void setSelected(@NotNull AnActionEvent e, boolean state) {
                    showDeclPath = state;
                    if (!showDeclPath) {
                        latestTargetElementInPath = null;
                        recolorGraph(false);
                        
                    } else {
                        findAndColorPath();
                    }
                }
            });
        }

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(title, actions, true);
        actionToolbar.setTargetComponent(this);
        return actionToolbar;
    }

    private FlexPanel createSecondToolbar() {
         FlexPanel panel = new FlexPanel(false);

        SimpleActionGroup actions = new SimpleActionGroup();
        actions.add(moduleAction = new ModuleComboAction("Logics Module:", Arrays.stream(LSFFileUtils.getModules(project)).collect(Collectors.toMap(Module::getName, Function.identity()))));
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(title, actions, true);
        actionToolbar.setTargetComponent(this);
        panel.add(actionToolbar.getComponent());

        if (showTargetField) {
            panel.add(new JLabel("Target LSF: "), FlexAlignment.CENTER);
            target = new SearchTextField() {

                @Override
                protected void onFieldCleared() {
                    super.onFieldCleared();
                    redrawCurrent();
                }
            };
            target.addKeyboardListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        redrawCurrent();
                    }
                }
            });
            panel.add(target);
        }
        return panel;
    }

    private ActionToolbar createThirdToolbar() {
        SimpleActionGroup actions = new SimpleActionGroup();

        actions.add(new BGTCheckboxAction("Manual update") {
            @Override
            public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
                return manualMode;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent anActionEvent, boolean state) {
                manualMode = state;
            }
        });

        actions.add(new AnAction("Refresh / Apply", "Refresh", LSFIcons.Design.REFRESH) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                redraw(true);
            }
        });

        actions.add(new Separator("|"));

        actions.add(new AnAction(LSFIcons.DEPENDENCY_ZOOM_OUT) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                zoom(1);
            }
        });
        
        actions.add(new AnAction(LSFIcons.DEPENDENCY_ACTUAL_ZOOM) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                zoom(0);
            }
        });

        actions.add(new AnAction(LSFIcons.DEPENDENCY_ZOOM_IN) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                zoom(-1);
            }
        });

        actions.add(new AnAction(LSFIcons.GRAPH_EXPORT) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (jgraph != null) {
                    new SVGExporter().exportSVG(jgraph);
                }
            }
        });

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(title, actions, true);
        actionToolbar.setTargetComponent(this);
        return actionToolbar;
    }

    public Collection<GraphNode> getAllNodes() {
        return dataModel.getNodes();
    }
    
    private void checkUpdate() {
        if (project.isDisposed()) return;

        final Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        final boolean insideToolwindow = SwingUtilities.isDescendingFrom(toolWindow.getComponent(), owner);
        if (insideToolwindow || JBPopupFactory.getInstance().isPopupActive()) {
            return;
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            final DataContext dataContext = DataManager.getInstance().getDataContext(owner);
            if (CommonDataKeys.PROJECT.getData(dataContext) != project) return;

            final VirtualFile[] files = hasFocus() ? null : CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);

            if (files != null && files.length == 1) {
                redraw();
            }
        });
    }

    public void redraw() {
        redraw(false);
    }

    public void redraw(boolean force) {
        getSelectedElement(newCurrentElement -> {
            if (newCurrentElement != null && (force || newCurrentElement != currentElement)) {
                currentElement = newCurrentElement;
                if (force || !manualMode) {
                    redrawCurrent(force);
                }
            }
        });
    }

    private void redrawCurrent() {
        redrawCurrent(false);
    }

    private void redrawCurrent(boolean force) {
        Component comp = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (comp != null) {
            remove(comp);
        }

        dataModel = new GraphDataModel();

        new Task.Backgroundable(project, "Calculating LSF Dependencies", true) {

            @Override
            public void onSuccess() {

                g = new ListenableDirectedGraph<>(DefaultEdge.class);

                fillGraph(getTargetText());

                m_jgAdapter = new JGraphModelAdapter(g);

                initJGraph();

                new DependencySpeedSearch(jgraph) {
                    @Override
                    protected GraphNode[] getNodes() {
                        return getAllNodes().toArray(new GraphNode[0]);
                    }

                    @Override
                    protected void selectElement(Object element, String selectedText) {
                        selectNode(isPopupActive() ? (GraphNode) element : null);
                        if (isPopupActive()) {
                            selectedIndex = Arrays.asList(nodes).indexOf(element);
                        }
                    }
                };

                if (!changeLayout(false)) {
                    return;
                }

                scrollPane = new JBScrollPane(jgraph);
                scrollPane.addMouseWheelListener(e -> {
                    if (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK) {
                        zoom(e.getWheelRotation());
                    }
                });

                add(scrollPane);

                initGraphDesign();

                recolorGraph(true);

                jgraph.refresh();

                Content content = toolWindow.getContentManager().getContent(DependenciesView.this);
                if (content != null && dataModel.rootNode != null) {
                    content.setDisplayName(dataModel.rootNode.getSID());
                }

                revalidate();

                if (force) {
                    //hack to fix small height after refresh
                    String currentLayout = layoutAction.getCurrentLayout();
                    changeLayout(TREE_LAYOUT, false);
                    changeLayout(currentLayout, false);
                }

                if(g.edgeSet().isEmpty() && !BaseUtils.isRedundantString(getTargetText())) {
                    ApplicationManager.getApplication().invokeLater(() -> JOptionPane.showMessageDialog(DependenciesView.this, getTargetText() + " not found in dependencies", title, JOptionPane.WARNING_MESSAGE));
                }

            }

            public void run(@NotNull ProgressIndicator indicator) {
                if (showRequired) {
                    createDependencyNode(currentElement, new HashSet<>());
                }

                if (showRequiring) {
                    createDependentNode(moduleAction.getCurrentModuleScope(), currentElement, new HashSet<>());
                }
            }
        }.queue();
    }

    private String getTargetText() {
        return target != null ? target.getText() : null;
    }

    protected void initJGraph() {
        jgraph = new JGraph(m_jgAdapter) {
            @Override
            protected void createBufferedImage(int width, int height) {
                try {
                    if (jgraph != null) {
                        super.createBufferedImage(width, height);
                    }
                } catch (IllegalArgumentException e) {
                    unableToDrawGraph();
                }
            }

            @Override
            public String getToolTipText(MouseEvent e) {
                if (jgraph != null) {
                    DefaultGraphCell cell = (DefaultGraphCell) jgraph.getFirstCellForLocation(e.getPoint().x, e.getPoint().y);
                    if (cell != null) {
                        Object userObject = cell.getUserObject();
                        if (userObject instanceof GraphNode) {
                            return ((GraphNode) userObject).getSID();
                        } else if (userObject instanceof GraphEdge) {
                            return ((GraphEdge) userObject).getSource().getSID() + " : " + ((GraphEdge) userObject).getTarget().getSID();
                        }
                    }
                }
                return null;
            }
        };

        jgraph.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK && e.getKeyCode() == KeyEvent.VK_0) {
                    zoom(0);
                }
            }
        });

        jgraph.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultGraphCell cell = (DefaultGraphCell) jgraph.getFirstCellForLocation(e.getPoint().x, e.getPoint().y);
                    if (cell != null) {
                        Object userObject = cell.getUserObject();
                        if (userObject instanceof GraphNode) {
                            ((GraphNode) userObject).navigate();
                        }
                    }
                }
            }
        });
        
        jgraph.setAntiAliased(true);
        jgraph.setEditable(false);
        jgraph.setAutoResizeGraph(true);
        jgraph.setScale(latestScale);    
    }
    
    private void zoom(int iterations) {
        if (jgraph != null) {
            if (iterations == 0) {
                latestScale = 1;
            } else {
                double scale = jgraph.getScale();
                latestScale = scale - (scale * iterations * 0.05);
            }
            jgraph.setScale(latestScale);
        }
    }

    private void changeEdges() {
        String currentEdges = edgesAction.getCurrentEdges();
        allEdges = currentEdges.equals(ALL_EDGES);
        onlyLeafs = currentEdges.equals(ONLY_LEAFS);
        if (!manualMode) {
            redrawCurrent();
        }
    }

    private boolean changeLayout(boolean update) {
        return changeLayout(layoutAction.getCurrentLayout(), update);
    }

    private boolean changeLayout(String currentLayout, boolean update) {
        if (update && (jgraph == null || jgraph.getParent() == null)) {
            redrawCurrent();
            return true;
        }

        JGraphLayout hir = null;
        switch (currentLayout) {
            case HIERARCHICAL_LAYOUT:
                hir = new JGraphHierarchicalLayout();
                ((JGraphHierarchicalLayout) hir).setOrientation(SwingConstants.WEST);
                ((JGraphHierarchicalLayout) hir).setInterRankCellSpacing(getAverageNodeWidth());
                break;
            case COMPACT_TREE_LAYOUT:
                hir = new JGraphCompactTreeLayout();
                ((JGraphCompactTreeLayout) hir).setPositionMultipleTrees(true);
                ((JGraphCompactTreeLayout) hir).setLevelDistance(getAverageNodeWidth());
                ((JGraphCompactTreeLayout) hir).setOrientation(SwingConstants.WEST);
                break;
            case TREE_LAYOUT:
                hir = new JGraphTreeLayout();
                ((JGraphTreeLayout) hir).setPositionMultipleTrees(true);
                ((JGraphTreeLayout) hir).setOrientation(SwingConstants.WEST);
                ((JGraphTreeLayout) hir).setLevelDistance(getAverageNodeWidth());
                break;
            case SIMPLE_LAYOUT:
                hir = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE);
                break;
            case ORGANIC_LAYOUT:
                hir = new JGraphOrganicLayout();
                break;
            case FAST_ORGANIC_LAYOUT:
                hir = new JGraphFastOrganicLayout();
                ((JGraphFastOrganicLayout) hir).setForceConstant(150);
                break;
            case RADIAL_TREE_LAYOUT:
                hir = new JGraphRadialTreeLayout();
                ((JGraphRadialTreeLayout) hir).setRadiusx(getAverageNodeWidth());
                break;
            case SELF_ORGANIZING_ORGANIC_LAYOUT:
                hir = new JGraphSelfOrganizingOrganicLayout();
                break;
        }

        try {
            final JGraphFacade graphFacade = new JGraphFacade(jgraph);
            assert hir != null;
            hir.run(graphFacade);
            final Map nestedMap = graphFacade.createNestedMap(true, true);
            jgraph.getGraphLayoutCache().edit(nestedMap);
            jgraph.getGraphLayoutCache().reload();

            ApplicationManager.getApplication().invokeLater(() -> scrollToNode(dataModel.rootNode));

        } catch (IllegalArgumentException e) {
            unableToDrawGraph();
            return false;
        }

        return true;
    }

    public void selectNode(GraphNode node) {
        if (selectedInSearch == null || !selectedInSearch.equals(node)) {
            selectedInSearch = node;
            recolorGraph(false);
            if (selectedInSearch != null) {
                scrollToNode(selectedInSearch);
            }
        }
    }

    public void scrollToNode(final GraphNode node) {
        if (scrollPane != null) {
            try {
                if (jgraph != null) {
                    DefaultGraphCell vertexCell = m_jgAdapter.getVertexCell(node);
                    if (vertexCell != null) {
                        Rectangle bounds = jgraph.getCellBounds(vertexCell).getBounds();
                        scrollPane.getHorizontalScrollBar().setValue(Math.min(bounds.x - 50, scrollPane.getHorizontalScrollBar().getMaximum()));
                        scrollPane.getVerticalScrollBar().setValue(Math.min(bounds.y - 50, scrollPane.getVerticalScrollBar().getMaximum()));
                    }
                }
            } catch (IllegalArgumentException e) {
                unableToDrawGraph();
            }
        }
    }

    public void fillGraph(String target) {
        for (GraphNode node : dataModel.getNodes()) {
            g.addVertex(node);
        }

        for (Pair<GraphNode, GraphNode> e : dataModel.getEdges()) {
            GraphEdge edge = new GraphEdge(e.first, e.second);
            g.addEdge(e.first, e.second, edge);
        }

        if(target != null && !target.isEmpty()) {
            Set<GraphNode> shortestPathNodes = new HashSet<>();
            if (showRequired) {
                List<GraphEdge> directPath = dataModel.getPath(g, target, false);
                if (directPath != null) {
                    directPath.forEach(graphEdge -> {
                        shortestPathNodes.add(graphEdge.getSource());
                        shortestPathNodes.add(graphEdge.getTarget());
                    });
                }
            }
            if (showRequiring) {
                List<GraphEdge> reversePath = dataModel.getPath(g, target, true);
                if (reversePath != null) {
                    reversePath.forEach(graphEdge -> {
                        shortestPathNodes.add(graphEdge.getSource());
                        shortestPathNodes.add(graphEdge.getTarget());
                    });
                }
            }

            for (GraphNode node : dataModel.getNodes()) {
                if(!shortestPathNodes.contains(node)) {
                    g.removeVertex(node);
                }
            }

            for (Pair<GraphNode, GraphNode> e : dataModel.getEdges()) {
                if(!shortestPathNodes.contains(e.first) || !shortestPathNodes.contains(e.second)) {
                    g.removeEdge(e.first, e.second);
                }
            }
        }
    }

    public void initGraphDesign() {
        Map<DefaultGraphCell, AttributeMap> cellAttr = new LinkedHashMap<>();
        for (GraphNode node : dataModel.getNodes()) {
            DefaultGraphCell cell = m_jgAdapter.getVertexCell(node);
            if (cell != null) {
                AttributeMap attr = cell.getAttributes();
                AttributeMap newAttr = new AttributeMap(attr);
                GraphConstants.setAutoSize(newAttr, true);
                GraphConstants.setInset(newAttr, 3);

                if (!newAttr.equals(attr)) {
                    cellAttr.put(cell, newAttr);
                }
            }
        }

        for (Object gEdge : g.edgeSet()) {
            DefaultGraphCell cell = m_jgAdapter.getEdgeCell(gEdge);
            if (cell != null) {
                AttributeMap attr = cell.getAttributes();
                AttributeMap newAttr = new AttributeMap(attr);
                GraphConstants.setLabelEnabled(newAttr, false);
                GraphConstants.setLineEnd(newAttr, GraphConstants.ARROW_CLASSIC);
                GraphConstants.setDisconnectable(newAttr, false);
                GraphConstants.setSelectable(newAttr, false);

                if (!newAttr.equals(attr)) {
                    cellAttr.put(cell, newAttr);
                }
            }
        }

        try {
            m_jgAdapter.edit(cellAttr, null, null, null);
        } catch (Exception ignored) {}
    }
    
    public void recolorGraph(boolean redraw) {
        recolorGraph(null, redraw);
    }

    public void recolorGraph(String targetElement, boolean redraw) {
        List<GraphEdge> path = null;
        if (targetElement != null) {
            path = dataModel.getPath(g, targetElement, false);
        }

        Map<DefaultGraphCell, AttributeMap> cellAttr = new LinkedHashMap<>();
        for (GraphNode node : dataModel.getNodes()) {
            DefaultGraphCell cell = m_jgAdapter.getVertexCell(node);
            if (cell != null) {
                AttributeMap attr = cell.getAttributes();

                AttributeMap newAttr = new AttributeMap(attr);
                Color background;

                if (node == selectedInSearch) {
                    background = new JBColor(new Color(247, 91, 107), DarculaColors.RED);
                } else if (node == dataModel.rootNode) {
                    background = getRootNodeColor();
                } else if (targetElement != null && node == dataModel.getNode(targetElement)) {
                    background = getPathTargetNodeColor();
                } else if (node.isDependent()) {
                    background = getDependentNodeColor(node);
                } else {
                    background = getDependencyNodeColor(node);
                }
                GraphConstants.setBackground(newAttr, background);
                
                if (redraw) {
                    Border nodeBorder = getNodeBorder(node);
                    if (nodeBorder != null) {
                        GraphConstants.setBorder(newAttr, new CompoundBorder(nodeBorder, GraphConstants.getBorder(attr)));
                    }
                }

                if (!newAttr.equals(attr)) {
                    cellAttr.put(cell, newAttr);
                }
            }
        }

        for (Object gEdge : g.edgeSet()) {
            DefaultGraphCell cell = m_jgAdapter.getEdgeCell(gEdge);
            if (cell != null) {
                AttributeMap attr = cell.getAttributes();

                AttributeMap newAttr = new AttributeMap(attr);
                GraphEdge edge = (GraphEdge) gEdge;
                if (path != null && path.contains(edge)) {
                    GraphConstants.setLineColor(newAttr, getPathEdgeColor());
                    GraphConstants.setLineWidth(newAttr, 2);
                } else if ((edge.getSource()).isDependent()) {
                    GraphConstants.setLineColor(newAttr, getDependentEdgeColor());
                    GraphConstants.setLineWidth(newAttr, 1);
                } else {
                    GraphConstants.setLineColor(newAttr, getDependencyEdgeColor());
                    GraphConstants.setLineWidth(newAttr, 1);
                }

                if (!newAttr.equals(attr)) {
                    cellAttr.put(cell, newAttr);
                }
            }
        }

        m_jgAdapter.edit(cellAttr, null, null, null);
    }
    
    public Color getRootNodeColor() {
        return new JBColor(new Color(255, 153, 0), new Color(255, 153, 0));
    }
    
    public Color getPathTargetNodeColor() {
        return new JBColor(new Color(114, 102, 255), new Color(114, 102, 255));    
    }
    
    public Color getDependentNodeColor(GraphNode node) {
        return getDependentEdgeColor();    
    }
    
    public Color getDependencyNodeColor(GraphNode node) {
        return getDependencyEdgeColor();
    }
    
    public Color getPathEdgeColor() {
        return getPathTargetNodeColor();
    }

    public Color getDependentEdgeColor() {
        return new JBColor(new Color(130, 184, 255), new Color(130, 184, 255));
    }

    public Color getDependencyEdgeColor() {
        return new JBColor(new Color(111, 195, 111), new Color(111, 195, 111));
    }
    
    public Border getNodeBorder(GraphNode node) {
        return null;
    }

    private void findAndColorPath() {
        getPathTarget(pathTarget -> {
            if (pathTarget != null && !pathTarget.equals(latestTargetElementInPath)) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    recolorGraph(pathTarget, false);
                    latestTargetElementInPath = pathTarget;
                });
            }
        });
    }

    private void unableToDrawGraph() {
        jgraph = null;
        
        ApplicationManager.getApplication().invokeLater(() -> JOptionPane.showMessageDialog(DependenciesView.this, "Unable to apply current layout", title, JOptionPane.WARNING_MESSAGE));
    }
    
    public void getTargetEditorPsiElement(Consumer<PsiElement> elementConsumer) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
                new Task.Backgroundable(project, "Getting target PSI element") {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        PsiElement psiLocation = ApplicationManager.getApplication().runReadAction((Computable<ConfigurationContext>) () -> ConfigurationContext.getFromContext(dataContext, ActionPlaces.UNKNOWN)).getPsiLocation();
                        elementConsumer.accept(psiLocation);
                    }
                }.queue();
            });
        }
        elementConsumer.accept(null);
    }

    public String getDependentTitle() {
        return "Dependents";
    }

    public String getDependencyTitle() {
        return "Dependencies";
    }

    public abstract void createDependencyNode(PsiElement element, Set<PsiElement> proceeded);

    public abstract void createDependentNode(GlobalSearchScope scope, PsiElement element, Set<PsiElement> proceeded);

    public abstract boolean showPathToElement();
    
    public abstract void getSelectedElement(Consumer<PsiElement> elementConsumer);
    
    public abstract void getPathTarget(Consumer<String> pathConsumer);
    
    public abstract int getAverageNodeWidth();

    @Override
    public void dispose() {
        //ignore
    }

    private abstract static class BGTCheckboxAction extends CheckboxAction {

        public BGTCheckboxAction(@NlsContexts.Checkbox String text) {
            super(text);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }
}

