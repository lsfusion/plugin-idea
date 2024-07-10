package com.lsfusion.design.view;

import com.intellij.designer.propertyTable.PropertyTable;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.ui.CheckboxTreeBase;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.Consumer;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.DesignInfo;
import com.lsfusion.design.model.*;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.ui.*;
import com.lsfusion.reports.ReportUtils;
import com.lsfusion.util.BaseUtils;
import lsfusion.server.physics.dev.debug.DebuggerService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.List;
import java.util.*;

import static com.lsfusion.debug.DebugUtils.debugProcess;
import static com.lsfusion.debug.DebugUtils.getDebuggerService;

public class EmbeddedDesign extends FormDesign {
    private ContainerView rootComponent;
    private FormEntity formEntity;
    private ComponentTreeNode rootNode;

    private DesignView.TargetForm targetForm;

    public String formCanonicalName;

    private DefaultActionGroup actions = new DefaultActionGroup();

    private JBScrollPane mainScrollPane;
    private JLayer<?> formLayer;
    private JPanel formPanel;
    private JLabel formNameLabel;
    private ActionToolbar toolbar;
    private ComponentTree componentTree;
    private PropertyTable propertyTable;
    private JBSplitter treeAndTable;
    private JButton createReportButton;
    private JButton editReportButton;
    private JButton deleteReportButton;
    
    private boolean selecting = false;
    
    private boolean highlighting = false;
    private List<Component> selectedComponents = new ArrayList<>();

    private final Map<ComponentView, JComponentPanel> componentToWidget = new HashMap<>();
    private final Map<JComponentPanel, ComponentView> widgetToComponent = new HashMap<>();
    private final Map<ComponentView, Boolean> selection = new HashMap<>();

    private final MergingUpdateQueue redrawQueue;

    private boolean firstDraw = true;

    public EmbeddedDesign(@NotNull Project project, final ToolWindowEx toolWindow) {
        super(project, toolWindow);

        redrawQueue = new MergingUpdateQueue("DesignView", 400, false, toolWindow.getComponent(), this, toolWindow.getComponent(), true);
        redrawQueue.setRestartTimerOnAdd(true);

        mainPanel = new FlexPanel(true);

        formNameLabel = new JLabel();
        formNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 9, 0, 9));

        toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actions, true); //ActionPlaces.TOOLBAR for suppress warning in log "Please do not use ActionPlaces.UNKNOWN or the empty place. Any string unique enough to deduce the toolbar location will do."
        
        treeAndTable = new JBSplitter(true);
        if (rootComponent != null) {
            treeAndTable.setFirstComponent(createComponentTree());
            treeAndTable.setSecondComponent(createComponentPropertyTable());
        }

        FlexPanel leftPanel = new FlexPanel(true);

        leftPanel.add(formNameLabel);
        leftPanel.add(toolbar.getComponent());
        leftPanel.add(treeAndTable, 1, FlexAlignment.STRETCH);

        formPanel = new JPanel(new BorderLayout());

        JBScrollPane scrollPane = new JBScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new JBColor(new Color(69, 160, 255), new Color(95, 123, 141))));
        formLayer = new JLayer(scrollPane, new SelectingLayerUI());

        JBSplitter formSplitter = new JBSplitter(false, 0.25f);
        formSplitter.setFirstComponent(leftPanel);
        formSplitter.setSecondComponent(formLayer);

        toolbar.setTargetComponent(formSplitter); // for suppressing error in log as there is a check "targetComponent == null", there will be an error "toolbar by default uses any focused component to update its actions. Toolbar actions that need local UI context would be incorrectly disabled. Please call toolbar.setTargetComponent() explicitly"
        mainPanel.add(formSplitter, new FlexConstraints(FlexAlignment.STRETCH, 1));
        mainPanel.add(createAutoReportPanel());

        mainScrollPane = new JBScrollPane(mainPanel);

        initUiHandlers();
    }

    @Override
    public JComponent getComponent() {
        return mainScrollPane;
    }
    
    public void onActivated() {
        if (!wasActivated) {
            wasActivated = true;
            DesignView.openFormUnderCaretDesign(project, (Consumer<DesignView.TargetForm>) this::scheduleRebuild);
        }
    }

    public void scheduleRebuild(PsiElement element, PsiFile file) {
        if (element == null || file == null || !DesignViewFactory.getInstance().windowIsVisible() || PsiDocumentManager.getInstance(project).getDocument(file) == null || DumbService.isDumb(project)) {
            return;
        }

        // as this event is called before commitTransaction, modificationStamp and unsavedDocument have not been updated, so the indexes cannot be accessed
        try {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

            if (editor != null) {
                DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
                new Task.Backgroundable(project, "Reading form") {
                    PsiElement targetElement;
                    DesignView.TargetForm targetForm;

                    @Override
                    public void onSuccess() {
                        if (targetForm != null && targetForm.module != null && targetForm.form != null) {
                            scheduleRebuild(targetForm);
                        }
                    }

                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        targetElement = DumbService.getInstance(project).runReadActionInSmartMode(() -> ConfigurationContext.getFromContext(dataContext, ActionPlaces.UNKNOWN).getPsiLocation());
                        targetForm = DesignView.getTargetForm(project, targetElement);
                    }
                }.queue();
            }
        } catch (PsiInvalidElementAccessException ignored) {
        }
    }

    public void scheduleRebuild(DesignView.TargetForm targetForm) {
        scheduleRebuild(targetForm, true);
    }

    public void scheduleRebuild(DesignView.TargetForm targetForm, boolean checkFormEquility) {
        scheduleRebuild("rebuildEmbedded", targetForm.file, checkFormEquility, formWithName -> {
            if (targetForm.form.isValid()) {
                EmbeddedDesign.this.targetForm = targetForm;
                
                formNameLabel.setText("Form: " + targetForm.form.getDeclName());

                layoutDesign(targetForm);
            }
        });
    }

    public void scheduleRedraw() {
        redrawQueue.queue(new Update("redrawEmbedded") {
            @Override
            public void run() {
                if (!project.isDisposed()) {
                    redrawComponents();
                }
            }
        });
    }

    private void layoutDesign(DesignView.TargetForm targetForm) {
        DesignInfo designInfo = DumbService.getInstance(project).runReadActionInSmartMode(() -> new DesignInfo(targetForm.form, targetForm.file, targetForm.localScope));

        ApplicationManager.getApplication().invokeLater(() -> {
            rootComponent = designInfo.formView.mainContainer;
            formEntity = designInfo.formView.entity;
            formCanonicalName = targetForm.form.getCanonicalName();

            treeAndTable.setFirstComponent(createComponentTree());
            treeAndTable.setSecondComponent(createComponentPropertyTable());
            redrawComponents();
        });
    }

    private void initUiHandlers() {
        actions.add(new ToggleAction("Show expert properties", "Show expert properties", LSFIcons.Design.EXPERT_PROPS) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return propertyTable != null && propertyTable.isShowExpertProperties();
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                if (propertyTable != null) {
                    propertyTable.showExpert(state);
                }
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        });

        actions.add(new ToggleAction("Select component", "Select component", LSFIcons.Design.FIND) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return selecting;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                selecting = state;
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        });

        actions.add(new ToggleAction("Highlight selected components", "Highlight selected components", LSFIcons.Design.HIGHLIGHT) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return highlighting;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                highlighting = state;
                mainPanel.repaint();
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        });

        actions.add(new AnAction("Update", "Update", LSFIcons.Design.REFRESH) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                DesignView.openFormUnderCaretDesign(project, (Consumer<DesignView.TargetForm>) targetForm -> scheduleRebuild(targetForm, false));
            }
        });

        toolbar.updateActionsImmediately();
    }

    private JComponent createAutoReportPanel() {
        createReportButton = new JButton("Create", LSFIcons.EDIT_AUTO_REPORT);
        createReportButton.setToolTipText("Create Default Report Design");

        editReportButton = new JButton("Edit", LSFIcons.EDIT_REPORT);
        editReportButton.setToolTipText("Edit Report Design");
        editReportButton.addActionListener(e -> {
            try {
                editReports();
            } catch (Exception ex) {
                JBPopupFactory.getInstance().createMessage("Can't open report files: " + ex.getMessage()).show(editReportButton);
            }
        });

        deleteReportButton = new JButton("Delete", LSFIcons.DELETE_REPORT);
        deleteReportButton.setToolTipText("Delete Report Design");
        deleteReportButton.addActionListener(e -> {
            deleteReports();
            redrawReportButtons();
        });

        FlexPanel reportsPanel = new FlexPanel(true);
        reportsPanel.add(new JLabel("Jasper Reports"));

        FlexPanel buttonsPanel = new FlexPanel(false);
        buttonsPanel.add(createReportButton);
        buttonsPanel.add(editReportButton);
        buttonsPanel.add(deleteReportButton);
        reportsPanel.add(buttonsPanel);

        return reportsPanel;
    }

    private void createReports(boolean hasReportFiles) throws NotBoundException, IOException {
        if (hasReportFiles) {
            deleteReports();
        }

        DebuggerService debuggerService = getDebuggerService();
        if (debuggerService != null) {
            String reportFiles = (String) debuggerService.evalServer("run() {createAutoReport('" + formCanonicalName + "');}");
            if (reportFiles != null) {
                for (String file : reportFiles.split(";")) {
                    Desktop.getDesktop().open(new File(file));
                }
            }
        }
    }

    private void editReports() throws IOException {
        for (PsiFile file : ReportUtils.findReportFiles(targetForm.form)) {
            String path = file.getVirtualFile().getCanonicalPath();
            if(path != null) {
                Desktop.getDesktop().open(new File(path));
            }
        }
    }

    private void deleteReports() {
        if (JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                "Are you sure you want to delete existing report design?", "Jasper reports", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (PsiFile file : ReportUtils.findReportFiles(targetForm.form)) {
                String path = file.getVirtualFile().getCanonicalPath();
                if(path != null) {
                    new File(path).delete();
                }
            }
        }
    }

    private JComponent createComponentPropertyTable() {
        propertyTable = new ComponentPropertyTable();
        propertyTable.update(Collections.singletonList(rootComponent), null);
        return new JBScrollPane(propertyTable);
    }

    private JComponent createComponentTree() {
        rootNode = createComponentNode(rootComponent, new HashSet<>());
        ComponentTreeCellRenderer renderer = new ComponentTreeCellRenderer();
        CheckboxTreeBase.CheckPolicy policy = new CheckboxTreeBase.CheckPolicy(true, true, false, false);
        componentTree = new ComponentTree(renderer, rootNode, policy);
        componentTree.setRootVisible(true);
        componentTree.expandRow(0);

        componentTree.addTreeSelectionListener(e -> {
            List<ComponentView> selectedComps = new ArrayList<>();
            selectedComponents.clear();

            for (ComponentTreeNode node : componentTree.getSelectedNodes(ComponentTreeNode.class, null)) {
                ComponentView component = node.getComponent();
                selectedComps.add(component);

                JComponent comp = componentToWidget.get(component);
                if (node.isChecked() && comp != null && comp.isShowing()) {
                    selectedComponents.add(comp);
                }
            }

            propertyTable.update(selectedComps, propertyTable.getSelectionProperty());
            if (highlighting) {
                mainPanel.repaint();
            }
        });

        componentTree.setCheckedListener(new CheckedListener() {
            @Override
            public void onNodeStateChanged(ComponentTreeNode node) {
                selection.put(node.getComponent(), node.isChecked());
                scheduleRedraw();
            }

            @Override
            public void nodeChecked(ComponentTreeNode node, boolean checked) {
                redrawComponents();
            }
        });
        
        return new JBScrollPane(componentTree);
    }

    private ComponentTreeNode createComponentNode(ComponentView component, Set<ComponentView> recursionGuard) {
        if (recursionGuard.contains(component))
            return null;
        else
            recursionGuard.add(component);
        boolean selected = defaultSelection(component);
        selection.put(component, selected);

        ComponentTreeNode node = new ComponentTreeNode(component);
        node.setChecked(selected);
        if (component instanceof ContainerView) {
            ContainerView container = (ContainerView) component;
            for (ComponentView child : container.getChildren()) {
                ComponentTreeNode childNode = createComponentNode(child, recursionGuard);
                if (childNode != null)
                    node.add(childNode);
            }
        }
        return node;
    }

    private boolean defaultSelection(ComponentView component) {
        if (component instanceof ClassChooserView || component instanceof FilterControlsView) {
            return false;
        } else if (component instanceof PropertyDrawView) {
            PropertyDrawView property = (PropertyDrawView) component;
            if (!property.isForcedPanel()) {
//                return false;
            }
        }
        return true;
    }

    private void redrawComponents() {
        formPanel.removeAll();

        JComponent rootWidget = rootComponent.createWidget(project, formEntity, selection, componentToWidget, new HashSet<>());
        widgetToComponent.clear();
        BaseUtils.reverse(componentToWidget, widgetToComponent);

        if (rootWidget != null) {
            formPanel.add(rootWidget);
        }

        redrawReportButtons();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void redrawReportButtons() {
        new Task.Backgroundable(project, "Updating design components") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                boolean hasReportFiles = DumbService.getInstance(project).runReadActionInSmartMode(() -> ReportUtils.hasReportFiles(targetForm.form));

                createReportButton.setText(hasReportFiles ? "Re-create" : "Create");
                for (ActionListener actionListener : createReportButton.getActionListeners()) {
                    createReportButton.removeActionListener(actionListener);
                }
                createReportButton.addActionListener(e -> {
                    if (debugProcess != null) {
                        try {
                            createReports(hasReportFiles);
                            redrawReportButtons();
                        } catch (Exception ex) {
                            JBPopupFactory.getInstance().createMessage("Can't create report files: " + ex.getMessage()).show(createReportButton);
                        }
                    } else {
                        JBPopupFactory.getInstance().createHtmlTextBalloonBuilder("Start server to create reports", MessageType.ERROR, null).createBalloon().showInCenterOf(createReportButton);
                    }
                });

                editReportButton.setEnabled(hasReportFiles);
                deleteReportButton.setEnabled(hasReportFiles);
            }
        }.queue();
    }

    private void selectInTree(ComponentView component) {
        if (componentTree == null) {
            return;
        }
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

        assert component.getContainer() != null;

        TreePath parentPath = getComponentPath(component.getContainer());
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
            if (selecting) {
                drawRectangle(g, currentWidget, JBColor.BLUE);
            } else if (highlighting) {
                for (Component component : selectedComponents) {
                    drawRectangle(g, component, JBColor.ORANGE);
                }
            }
        }
        
        private void drawRectangle(Graphics g, Component component, Color color) {
            if (component != null && component.getParent() != null) {
                Rectangle rect = component.getBounds();
                rect = SwingUtilities.convertRectangle(component.getParent(), rect, formLayer);

                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                g2.setColor(color);
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
                switch (e.getID()) {
                    case MouseEvent.MOUSE_CLICKED:
                        currentWidget = null;
                        selecting = false;
                        mainPanel.repaint();
                        break;
                    case MouseEvent.MOUSE_ENTERED:
                    case MouseEvent.MOUSE_EXITED:
                    case MouseEvent.MOUSE_MOVED:
                    case MouseEvent.MOUSE_DRAGGED:
                        Point p = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), formPanel);
                        ComponentView component = getDeepestComponentAt(formPanel, p.x, p.y);
                        currentWidget = componentToWidget.get(component);
                        selectInTree(component);
                        mainPanel.repaint();
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
            Component[] components = parent.getComponents();
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
