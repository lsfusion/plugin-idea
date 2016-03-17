package com.lsfusion.design.ui;

import com.intellij.designer.propertyTable.PropertyTable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.tools.SimpleActionGroup;
import com.intellij.ui.CheckboxTreeBase;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.DesignInfo;
import com.lsfusion.design.model.*;
import com.lsfusion.design.vfs.LSFDesignVirtualFileImpl;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.LayerUI;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import static javax.swing.BorderFactory.*;

/*
    TODO:
    - выключить по умолчанию
        - свойства, за исключением тех, которые FORCE PANEL
        - formEdit, formDrop, formOk, formClose
            - просто не делать им force panel при создании
    - просетать при создании модели
        - RegularFilterGroupView.isSingle
        - forceHide
            - гриды, тулбары, фильтры, showtype для групп дерева
    - учесть CUSTOM (раньше: отсутствие FROM DEFAULT)
 */
public class EditorPanel extends JPanel {

    @NotNull
    private final Project project;
    private LSFDesignVirtualFileImpl file;
    private ContainerView rootComponent;
    private ComponentTreeNode rootNode;

    private SimpleActionGroup actions = new SimpleActionGroup();
            
    private JLayer formLayer;
    private JPanel formPanel;
    private ActionToolbar toolbar;
    private ComponentTree componentTree;
    private PropertyTable propertyTable;
    private boolean selecting = false;

    private final Map<ComponentView, JComponent> componentToWidget = new HashMap<>();
    private final Map<JComponent, ComponentView> widgetToComponent = new HashMap<>();
    private final Map<ComponentView, Boolean> selection = new HashMap<>();

    public EditorPanel(@NotNull Project project, @NotNull LSFDesignVirtualFileImpl file) {
        super(null);
        
        this.project = project;
        this.file = file;

        rootComponent = file.getDesignInfo().formView.mainContainer;

        createLayout();
        initUiHandlers();
    }

    private void initUiHandlers() {
        actions.add(new ToggleAction(null, "Show expert properties", AllIcons.General.Filter) {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return propertyTable.isShowExpertProperties();
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                propertyTable.showExpert(state);
            }
        });
        
        actions.add(new ToggleAction(null, "Select component", LSFIcons.Design.FIND) {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return selecting;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                selecting = state;
            }
        });

        actions.add(new AnAction(null, "Refresh", LSFIcons.Design.REFRESH) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                DesignInfo designInfo = file.getDesignInfo();
                if (designInfo != null) {
                    removeAll();
                    rootComponent = designInfo.formView.mainContainer;
                    createLayout();
                    initListeners();
                }
            }
        });

        toolbar.updateActionsImmediately();

        initListeners();
    }

    private void initListeners() {
        componentTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                List<ComponentView> selectedComponents = new ArrayList<>();
                for (ComponentTreeNode node : componentTree.getSelectedNodes(ComponentTreeNode.class, null)) {
                    selectedComponents.add(node.getComponent());
                }
                propertyTable.update(selectedComponents, propertyTable.getSelectionProperty());
            }
        });
        
        componentTree.setCheckedListener(new CheckedListener() {
            @Override
            public void onNodeStateChanged(ComponentTreeNode node) {
                selection.put(node.getComponent(), node.isChecked());
            }

            @Override
            public void nodeChecked(ComponentTreeNode node, boolean checked) {
                rebuildForm();
            }
        });
    }

    private void createLayout() {
        JBSplitter treeAndTable = new JBSplitter(true);
        treeAndTable.setFirstComponent(createComponentTree());
        treeAndTable.setSecondComponent(createComponentPropertyTable());

        toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actions, true);

        FlexPanel leftPanel = new FlexPanel(true);
        leftPanel.add(toolbar.getComponent());
        leftPanel.add(treeAndTable, 1, FlexAlignment.STRETCH);

        formPanel = new JPanel(new BorderLayout());

        formLayer = new JLayer(formPanel, new SelectingLayerUI());

        JBSplitter formSplitter = new JBSplitter(false, 0.25f);
        formSplitter.setFirstComponent(leftPanel);
        formSplitter.setSecondComponent(formLayer);
        
        rebuildForm();
        
        setLayout(new BorderLayout());
        add(formSplitter);
    }

    private JComponent createComponentPropertyTable() {
        propertyTable = new ComponentPropertyTable();
        propertyTable.update(Arrays.asList(rootComponent), null);
        return new JBScrollPane(propertyTable);
    }

    private JComponent createComponentTree() {
        rootNode = createComponentNode(rootComponent);
        ComponentTreeCellRenderer renderer = new ComponentTreeCellRenderer();
        CheckboxTreeBase.CheckPolicy policy = new CheckboxTreeBase.CheckPolicy(true, true, false, false);
        componentTree = new ComponentTree(renderer, rootNode, policy);
        componentTree.expandRow(0);
        return new JBScrollPane(componentTree);
    }

    private ComponentTreeNode createComponentNode(ComponentView component) {
        boolean selected = defaultSelection(component);
        selection.put(component, selected);

        ComponentTreeNode node = new ComponentTreeNode(component);
        node.setChecked(selected);
        if (component instanceof ContainerView) {
            ContainerView container = (ContainerView) component;
            for (ComponentView child : container.getChildren()) {
                ComponentTreeNode childNode = createComponentNode(child);
                node.add(childNode);
            }
        }
        return node;
    }

    private boolean defaultSelection(ComponentView component) {
        if (component instanceof FilterView) {
            return false;
        } else if (component instanceof ClassChooserView) {
            return false;
        } else if (component instanceof PropertyDrawView) {
            PropertyDrawView property = (PropertyDrawView) component;
            if (!property.isForcedPanel()) {
//                return false;
            }
        }
        return true;
    }

    private void rebuildForm() {
        formPanel.removeAll();

        JComponent rootWidget = rootComponent.createWidget(project, selection, componentToWidget);
        widgetToComponent.clear();
        BaseUtils.reverse(componentToWidget, widgetToComponent);
        
        if (rootWidget != null) {
            rootWidget.setBorder(
                    createCompoundBorder(createEmptyBorder(5, 5, 5, 5), createLineBorder(new Color(69, 160, 255), 1))
            );
            formPanel.add(rootWidget);
        }
        revalidate();
        repaint();
    }

    private void selectInTree(ComponentView component) {
        ComponentTreeNode[] nodes = componentTree.getSelectedNodes(ComponentTreeNode.class, null);
        if ((nodes.length == 1 && nodes[0].getComponent() == component) || component == null) {
            return;
        }
        
        TreePath path = getComponentPath(component);
        
        componentTree.expandPath(path);
        componentTree.scrollPathToVisible(path);
        componentTree.setSelectionPath(path);
    }

    private TreePath getComponentPath(ComponentView component) {
        if (component == rootComponent) {
            return new TreePath(rootNode);
        }
        
        assert component.getParent() != null;
        
        TreePath parentPath = getComponentPath(component.getParent());
        ComponentTreeNode parentNode = (ComponentTreeNode) parentPath.getLastPathComponent();
        for (int i = 0; i < parentNode.getChildCount(); ++i) {
            ComponentTreeNode childNode = (ComponentTreeNode) parentNode.getChildAt(i);
            if (childNode.getComponent() == component) {
                return parentPath.pathByAddingChild(childNode);
            }
        }
        
        throw new IllegalStateException("shouldn't happen");
    }

    private class SelectingLayerUI extends LayerUI {
        private Component currentWidget;

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            JLayer l = (JLayer) c;
            l.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        }

        @Override
        public void uninstallUI(JComponent c) {
            super.uninstallUI(c);
            JLayer l = (JLayer) c;
            l.setLayerEventMask(0);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);
            if (selecting && currentWidget != null && currentWidget.getParent() != null) {
                Rectangle rect = currentWidget.getBounds();
                rect = SwingUtilities.convertRectangle(currentWidget.getParent(), rect, formLayer);

                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                g2.setColor(JBColor.BLUE);
                g2.drawRect(rect.x, rect.y, rect.width, rect.height);
            }
        }

        @Override
        public void eventDispatched(AWTEvent e, JLayer l) {
            if (!selecting) {
                return;
            }
            
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) e;
                me.consume();
                switch(e.getID()) {
                    case MouseEvent.MOUSE_CLICKED:
                        currentWidget = null;
                        selecting = false;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_ENTERED:
                    case MouseEvent.MOUSE_EXITED:
                    case MouseEvent.MOUSE_MOVED:
                    case MouseEvent.MOUSE_DRAGGED:
                        Point p = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), formPanel);
                        ComponentView component = getDeepestComponentAt(formPanel, p.x, p.y);
                        currentWidget = componentToWidget.get(component);
                        selectInTree(component);
                        repaint();
                        break;
                }
            } else if (e instanceof KeyEvent) {
                ((KeyEvent) e).consume();
            }
        }

        public ComponentView getDeepestComponentAt(@NotNull JComponent parent, int x, int y) {
            if (!parent.contains(x, y)) {
                return null;
            }
            Component components[] = parent.getComponents();
            for (Component comp : components) {
                if (comp != null && comp.isVisible() && comp instanceof JComponent) {
                    Point loc = comp.getLocation();
                    ComponentView result = getDeepestComponentAt((JComponent) comp, x - loc.x, y - loc.y);
                    if (result != null) {
                        return result;
                    }
                }
            }
            
            return widgetToComponent.get(parent);
        }
    }
}
