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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.tools.SimpleActionGroup;
import com.intellij.ui.DarculaColors;
import com.intellij.ui.JBColor;
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
import java.util.*;
import java.util.Timer;

import static com.lsfusion.dependencies.GraphLayoutComboAction.*;

public abstract class DependenciesView extends JPanel implements Disposable {
    protected String title;

    protected final Project project;
    protected ToolWindowEx toolWindow;

    protected PsiElement currentElement;

    protected boolean showRequired = true;
    protected boolean showRequiring = false;
    protected boolean allEdges = false;

    protected boolean showDeclPath = false;
    protected String latestTargetElementInPath;

    protected GraphDataModel dataModel;
    
    protected JGraph jgraph;
    protected ListenableDirectedGraph g;
    protected JGraphModelAdapter m_jgAdapter;
    protected JBScrollPane scrollPane;
    
    protected GraphNode selectedInSearch;

    protected CheckboxAction showRequiredAction;
    protected CheckboxAction showRequiringAction;
    protected GraphLayoutComboAction layoutAction;
    
    protected double latestScale = 1;

    public DependenciesView(String title, Project project, final ToolWindowEx toolWindow) {
        this.title = title;
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
                    if (showPathToElement() && showDeclPath) {
                        findAndColorPath();
                    }
                }
            }
        };
        ActionManager.getInstance().addTimerListener(500, timerListener);
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!toolWindow.isVisible() && jgraph != null && jgraph.getOffscreen() != null && jgraph.getOffgraphics() != null) {
                    jgraph.releaseOffscreenResources();
                }
            }
        }, 0, 5 * 60 * 1000);

        ActionToolbar toolbar = createToolbar();

        toolbar.updateActionsImmediately();

        add(toolbar.getComponent(), BorderLayout.NORTH);

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

        showRequiringAction = new CheckboxAction(getDependentTitle()) {
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

        showRequiredAction = new CheckboxAction(getDependencyTitle()) {
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

        layoutAction = new GraphLayoutComboAction("Layout:") {
            @Override
            protected void changeLayout(boolean update) {
                DependenciesView.this.changeLayout(update);
            }
        };
        actions.add(layoutAction);

        if (showPathToElement()) {
            actions.add(new CheckboxAction("Path to element") {
                @Override
                public boolean isSelected(AnActionEvent e) {
                    return showDeclPath;
                }

                @Override
                public void setSelected(AnActionEvent e, boolean state) {
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

        actions.add(new AnAction(LSFIcons.DEPENDENCY_ZOOM_OUT) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                zoom(1);
            }
        });
        
        actions.add(new AnAction(LSFIcons.DEPENDENCY_ACTUAL_ZOOM) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                zoom(0);
            }
        });

        actions.add(new AnAction(LSFIcons.DEPENDENCY_ZOOM_IN) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                zoom(-1);
            }
        });

        actions.add(new AnAction(LSFIcons.GRAPH_EXPORT) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                if (jgraph != null) {
                    new SVGExporter().exportSVG(jgraph);
                }
            }
        });

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actions, true);
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

        final DataContext dataContext = DataManager.getInstance().getDataContext(owner);
        if (CommonDataKeys.PROJECT.getData(dataContext) != project) return;

        final VirtualFile[] files = hasFocus() ? null : CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);

        if (files != null && files.length == 1) {
            redraw();
        }
    }

    private void redraw() {
        PsiElement newCurrentElement = getSelectedElement();
        if (newCurrentElement != null && newCurrentElement != currentElement) {
            currentElement = newCurrentElement;
            redrawCurrent();
        }
    }

    private void redrawCurrent() {
        Component comp = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (comp != null) {
            remove(comp);
        }

        dataModel = new GraphDataModel();

        if (showRequired) {
            createDependencyNode(currentElement, new HashSet<PsiElement>());
        }

        if (showRequiring) {
            createDependentNode(currentElement, new HashSet<PsiElement>());
        }

        g = new ListenableDirectedGraph(DefaultEdge.class);

        fillGraph();

        m_jgAdapter = new JGraphModelAdapter(g);

        initJGraph();

        new DependencySpeedSearch(jgraph) {
            @Override
            protected GraphNode[] getNodes() {
                return getAllNodes().toArray(new GraphNode[getAllNodes().size()]);
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
        add(scrollPane);

        initGraphDesign();

        recolorGraph(true);

        jgraph.refresh();

        Content content = toolWindow.getContentManager().getContent(this);
        if (content != null && dataModel.rootNode != null) {
            content.setDisplayName(dataModel.rootNode.getSID());
        }
        
        revalidate();
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
                if (e.getModifiers() == InputEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_0) {
                    zoom(0);
                }
            }
        });

        jgraph.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK) {
                    zoom(e.getWheelRotation());
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

    private boolean changeLayout(boolean update) {
        if (update && (jgraph == null || jgraph.getParent() == null)) {
            redrawCurrent();
            return true;
        }

        JGraphLayout hir = null;
        switch (layoutAction.getCurrentLayout()) {
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

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    scrollToNode(dataModel.rootNode);
                }
            });

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

    public void fillGraph() {
        for (GraphNode node : dataModel.getNodes()) {
            g.addVertex(node);
        }

        for (Pair<GraphNode, GraphNode> e : dataModel.getEdges()) {
            GraphEdge edge = new GraphEdge(e.first, e.second);
            g.addEdge(e.first, e.second, edge);
        }
    }

    public void initGraphDesign() {
        Map cellAttr = new HashMap();
        for (GraphNode node : dataModel.getNodes()) {
            DefaultGraphCell cell = m_jgAdapter.getVertexCell(node);
            if (cell != null) {
                Map attr = cell.getAttributes();
                Map newAttr = new AttributeMap(attr);
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
                Map attr = cell.getAttributes();
                Map newAttr = new AttributeMap(attr);
                GraphConstants.setLabelEnabled(newAttr, false);
                GraphConstants.setLineEnd(newAttr, GraphConstants.ARROW_CLASSIC);
                GraphConstants.setDisconnectable(newAttr, false);
                GraphConstants.setSelectable(newAttr, false);

                if (!newAttr.equals(attr)) {
                    cellAttr.put(cell, newAttr);
                }
            }
        }

        m_jgAdapter.edit(cellAttr, null, null, null);
    }
    
    public void recolorGraph(boolean redraw) {
        recolorGraph(null, redraw);
    }

    public void recolorGraph(String targetElement, boolean redraw) {
        java.util.List path = null;
        if (targetElement != null) {
            path = dataModel.getPath(g, targetElement);
        }

        Map cellAttr = new HashMap();
        for (GraphNode node : dataModel.getNodes()) {
            DefaultGraphCell cell = m_jgAdapter.getVertexCell(node);
            if (cell != null) {
                Map attr = cell.getAttributes();

                Map newAttr = new AttributeMap(attr);
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
                Map attr = cell.getAttributes();

                Map newAttr = new AttributeMap(attr);
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
        String pathTarget = getPathTarget();
        if (pathTarget != null && !pathTarget.equals(latestTargetElementInPath)) {
            recolorGraph(pathTarget, false);
            latestTargetElementInPath = pathTarget;
        }
    }

    private void unableToDrawGraph() {
        jgraph = null;
        
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(DependenciesView.this, "Unable to apply current layout", title, JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    public PsiElement getTargetEditorPsiElement() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
            return ConfigurationContext.getFromContext(dataContext).getPsiLocation();
        }
        return null;
    }

    public String getDependentTitle() {
        return "Dependents";
    }

    public String getDependencyTitle() {
        return "Dependencies";
    }

    public abstract void createDependencyNode(PsiElement element, Set<PsiElement> proceeded);

    public abstract void createDependentNode(PsiElement element, Set<PsiElement> proceeded);

    public abstract boolean showPathToElement();
    
    public abstract PsiElement getSelectedElement();
    
    public abstract String getPathTarget();
    
    public abstract int getAverageNodeWidth();

    @Override
    public void dispose() {
        //ignore
    }
}

