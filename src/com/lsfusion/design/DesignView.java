package com.lsfusion.design;

import com.intellij.designer.propertyTable.PropertyTable;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.tools.SimpleActionGroup;
import com.intellij.ui.CheckboxTreeBase;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.model.*;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.ui.*;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFLocalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormStatementImpl;
import com.lsfusion.reports.ReportUtils;
import com.lsfusion.util.BaseUtils;
import lsfusion.server.physics.dev.debug.DebuggerService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.List;
import java.util.*;

import static com.lsfusion.debug.DebugUtils.debugProcess;
import static com.lsfusion.debug.DebugUtils.getDebuggerService;

public class DesignView extends JPanel implements Disposable {
    @NotNull
    private final Project project;
    private final ToolWindowEx toolWindow;
    private ContainerView rootComponent;
    private FormEntity formEntity;
    private ComponentTreeNode rootNode;

    private LSFModuleDeclaration module;
    private LSFFormDeclaration formDeclaration;
    private LSFLocalSearchScope localScope;

    public String formTitle;
    public String formCanonicalName;

    private SimpleActionGroup actions = new SimpleActionGroup();

    private JLayer<?> formLayer;
    private JPanel formPanel;
    private ActionToolbar toolbar;
    private ComponentTree componentTree;
    private PropertyTable propertyTable;
    private boolean selecting = false;
    
    private boolean highlighting = false;
    private List<Component> selectedComponents = new ArrayList<>();

    private final Map<ComponentView, JComponentPanel> componentToWidget = new HashMap<>();
    private final Map<JComponentPanel, ComponentView> widgetToComponent = new HashMap<>();
    private final Map<ComponentView, Boolean> selection = new HashMap<>();

    private final MergingUpdateQueue myUpdateQueue;
    private final MergingUpdateQueue redrawQueue;

    private boolean firstDraw = true;

    public DesignView(@NotNull Project project, final ToolWindowEx toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        myUpdateQueue = new MergingUpdateQueue("DesignView", 150, false, toolWindow.getComponent(), this, toolWindow.getComponent(), true);
        myUpdateQueue.setRestartTimerOnAdd(true);

        redrawQueue = new MergingUpdateQueue("DesignView", 150, false, toolWindow.getComponent(), this, toolWindow.getComponent(), true);
        redrawQueue.setRestartTimerOnAdd(true);

        final TimerListener timerListener = new TimerListener() {
            @Override
            public ModalityState getModalityState() {
                return ModalityState.stateForComponent(toolWindow.getComponent());
            }

            @Override
            public void run() {
                if (toolWindow.isVisible()) {
                    checkUpdate();
                }
            }
        };
        ActionManager.getInstance().addTimerListener(timerListener);
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
            changeForm(files[0], dataContext);
        }
    }

    private void changeForm(VirtualFile file, DataContext dataContext) {
        if (file == null) {
            return;
        }

        PsiElement targetElement = ConfigurationContext.getFromContext(dataContext).getPsiLocation();

        if (targetElement != null) {
            LSFFormDeclaration formDeclaration = null;
            LSFLocalSearchScope localScope = null;
            LSFFormExtend formExtend = PsiTreeUtil.getParentOfType(targetElement, LSFFormExtend.class);
            if (formExtend != null) {
                localScope = LSFLocalSearchScope.createFrom(formExtend);
                formDeclaration = DumbService.getInstance(project).runReadActionInSmartMode(((LSFFormStatementImpl) formExtend)::resolveFormDecl);
            } else {
                LSFDesignStatement designStatement = PsiTreeUtil.getParentOfType(targetElement, LSFDesignStatement.class);
                if (designStatement != null) {
                    localScope = LSFLocalSearchScope.createFrom(designStatement);
                    formDeclaration = DumbService.getInstance(project).runReadActionInSmartMode(designStatement::resolveFormDecl);
                }
            }

            LSFModuleDeclaration module = null;
            String formName = formDeclaration != null ? formDeclaration.getDeclName() : null;

            PsiFile containingFile = targetElement.getContainingFile();
            if (containingFile instanceof LSFFile) {
                module = ((LSFFile) containingFile).getModuleDeclaration();
            }

            if (formName != null && module != null) {
                if (formDeclaration != this.formDeclaration || module != this.module || !BaseUtils.nullEquals(localScope, this.localScope)) {
                    scheduleRebuild(module, formDeclaration, localScope);
                }
            }
        }
    }

    public void scheduleRebuild(final LSFModuleDeclaration module, final LSFFormDeclaration formDeclaration, LSFLocalSearchScope localScope) {
        myUpdateQueue.queue(new Update("rebuild") {
            @Override
            public void run() {
                if (!project.isDisposed() && formDeclaration.isValid()) {
                    update(module, formDeclaration, localScope);
                }
            }
        });
    }         
    
    public void scheduleRedraw() {
        redrawQueue.queue(new Update("redraw") {
            @Override
            public void run() {
                if (!project.isDisposed()) {
                    redrawForm();
                }
            }
        });
    }

    public void update(LSFModuleDeclaration module, LSFFormDeclaration formDeclaration, LSFLocalSearchScope localScope) {
        this.module = module;
        this.formDeclaration = formDeclaration;
        this.localScope = localScope;

        Content content = toolWindow.getContentManager().getContent(this);
        if (content != null) {
            content.setDisplayName(formDeclaration.getDeclName());
        }
        
        layoutDesign(module, formDeclaration, localScope);
    }

    private void layoutDesign(LSFModuleDeclaration module, LSFFormDeclaration formDeclaration, LSFLocalSearchScope localScope) {
        DesignInfo designInfo = new DesignInfo(formDeclaration, module.getLSFFile(), localScope);

        removeAll();
        rootComponent = designInfo.formView.mainContainer;
        formEntity = designInfo.formView.entity;
        formTitle = designInfo.getFormCaption();
        formCanonicalName = formDeclaration.getCanonicalName();
        createLayout();

        if (firstDraw) {
            initUiHandlers();
            firstDraw = false;
        } else {
            initListeners();
        }
    }

    private void initUiHandlers() {
        actions.add(new ToggleAction(null, "Show expert properties", LSFIcons.Design.EXPERT_PROPS) {
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
        
        actions.add(new ToggleAction(null, "Highlight selected components", LSFIcons.Design.HIGHLIGHT) {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return highlighting;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                highlighting = state;
                repaint();
            }
        });

        actions.add(new AnAction(null, "Refresh", LSFIcons.Design.REFRESH) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                refresh();
            }
        });

        toolbar.updateActionsImmediately();

        initListeners();
    }

    private void refresh() {
        layoutDesign(module, formDeclaration, localScope);
    }

    private void initListeners() {
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
                repaint();
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
                redrawForm();
            }
        });
    }

    private static final String LSF_PROPERTY_LIVE_FORM_DESIG_EDITING_ON = "lsfusion.property.live.form.design.editing.on";
    private TimerListener timerListener;
    private FlexPanel liveFormDesignViewPanel;
    public static boolean isLiveFormDesignEditingEnable(Project project) {
        return project != null && PropertiesComponent.getInstance(project).getBoolean(LSF_PROPERTY_LIVE_FORM_DESIG_EDITING_ON, false);
    }

    private FlexPanel createLiveFormDesignViewPanel() {
        JButton showForm = new JButton("Show form");
        showForm.setMnemonic('D');
        showForm.setToolTipText("Click or press Alt+D");
        showForm.addActionListener(e -> {
            Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (selectedTextEditor != null) {
                PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(selectedTextEditor.getDocument());
                if (file != null) {
                    PsiElement element = file.findElementAt(selectedTextEditor.getCaretModel().getOffset());
                    if (element != null)
                        FormDesignChangeDetector.showLiveDesign(project, element, file);
                }
            }
        });

        JCheckBox enableLiveFormDesignEditing = new JCheckBox("Enable live form design editing", isLiveFormDesignEditingEnable(project));
        enableLiveFormDesignEditing.addActionListener(e -> {
            boolean liveFormDesignEditingEnable = isLiveFormDesignEditingEnable(project);
            if (liveFormDesignEditingEnable) {
                if (timerListener != null) {
                    ActionManager.getInstance().removeTimerListener(timerListener);
                    timerListener = null;
                }
                showForm.setEnabled(true);
            } else if (timerListener == null) {
                timerListener = new TimerListener() {
                    @Override
                    public ModalityState getModalityState() {
                        return ModalityState.defaultModalityState();
                    }

                    @Override
                    public void run() {
                        PsiElement element = ConfigurationContext.getFromContext(DataManager.getInstance()
                                .getDataContext(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner())).getPsiLocation();

                        if (element != null)
                            FormDesignChangeDetector.showLiveDesign(project, element, element.getContainingFile());
                    }
                };

                ActionManager.getInstance().addTimerListener(timerListener);
                showForm.setEnabled(false);
            }
            PropertiesComponent.getInstance(project).setValue(LSF_PROPERTY_LIVE_FORM_DESIG_EDITING_ON, Boolean.toString(!liveFormDesignEditingEnable));
        });

        FlexPanel flexPanel = new FlexPanel(false);
        flexPanel.add(enableLiveFormDesignEditing, FlexAlignment.CENTER);
        flexPanel.add(showForm);
        flexPanel.setBorder(BorderFactory.createLineBorder(new JBColor(new Color(69, 160, 255), new Color(95, 123, 141))));

        return flexPanel;
    }

    private FlexPanel createAutoReportPanel() {
        boolean hasReportFiles = ReportUtils.hasReportFiles(formDeclaration);

        JButton createReportButton = new JButton(hasReportFiles ? "Re-create" : "Create", LSFIcons.EDIT_AUTO_REPORT);
        createReportButton.setToolTipText("Create Default Report Design");
        createReportButton.addActionListener(e -> {
            if(debugProcess != null) {
                try {
                    createReports(hasReportFiles);
                    refresh();
                } catch (Exception ex) {
                    JBPopupFactory.getInstance().createMessage("Can't create report files: " + ex.getMessage()).show(createReportButton);
                }
            } else {
                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder("Start server to create reports", MessageType.ERROR, null).createBalloon().showInCenterOf(createReportButton);
            }
        });

        JButton editReportButton = new JButton("Edit", LSFIcons.EDIT_REPORT);
        editReportButton.setToolTipText("Edit Report Design");
        editReportButton.addActionListener(e -> {
            try {
                editReports();
            } catch (Exception ex) {
                JBPopupFactory.getInstance().createMessage("Can't open report files: " + ex.getMessage()).show(editReportButton);
            }
        });
        if (!hasReportFiles) {
            editReportButton.setEnabled(false);
        }

        JButton deleteReportButton = new JButton("Delete", LSFIcons.DELETE_REPORT);
        deleteReportButton.setToolTipText("Delete Report Design");
        deleteReportButton.addActionListener(e -> {
            deleteReports();
            refresh();
        });
        if (!hasReportFiles) {
            deleteReportButton.setEnabled(false);
        }

        FlexPanel flexPanel = new FlexPanel(true);
        flexPanel.add(new JLabel("Jasper Reports"));

        FlexPanel buttonsPanel = new FlexPanel(false);
        buttonsPanel.add(createReportButton);
        buttonsPanel.add(editReportButton);
        buttonsPanel.add(deleteReportButton);
        flexPanel.add(buttonsPanel);

        return flexPanel;
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
        for (PsiFile file : ReportUtils.findReportFiles(formDeclaration)) {
            String path = file.getVirtualFile().getCanonicalPath();
            if(path != null) {
                Desktop.getDesktop().open(new File(path));
            }
        }
    }

    private void deleteReports() {
        if (JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                "Are you sure you want to delete existing report design?", "Jasper reports", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (PsiFile file : ReportUtils.findReportFiles(formDeclaration)) {
                String path = file.getVirtualFile().getCanonicalPath();
                if(path != null) {
                    new File(path).delete();
                }
            }
        }
    }

    private void createLayout() {
        JBSplitter treeAndTable = new JBSplitter(true);
        treeAndTable.setFirstComponent(createComponentTree());
        treeAndTable.setSecondComponent(createComponentPropertyTable());

        toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actions, true); //ActionPlaces.TOOLBAR for suppress warning in log "Please do not use ActionPlaces.UNKNOWN or the empty place. Any string unique enough to deduce the toolbar location will do."

        FlexPanel leftPanel = new FlexPanel(true);

        if(liveFormDesignViewPanel == null)
            liveFormDesignViewPanel = createLiveFormDesignViewPanel();

        leftPanel.add(liveFormDesignViewPanel);

        leftPanel.add(createAutoReportPanel());

        leftPanel.add(toolbar.getComponent());
        leftPanel.add(treeAndTable, 1, FlexAlignment.STRETCH);

        formPanel = new JPanel(new BorderLayout());

        JBScrollPane scrollPane = new JBScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new JBColor(new Color(69, 160, 255), new Color(95, 123, 141))));
        formLayer = new JLayer(scrollPane, new SelectingLayerUI());

        JBSplitter formSplitter = new JBSplitter(false, 0.25f);
        formSplitter.setFirstComponent(leftPanel);
        formSplitter.setSecondComponent(formLayer);

        redrawForm();

        setLayout(new BorderLayout());
        toolbar.setTargetComponent(formSplitter); // for suppressing error in log as there is a check "targetComponent == null", there will be an error "toolbar by default uses any focused component to update its actions. Toolbar actions that need local UI context would be incorrectly disabled. Please call toolbar.setTargetComponent() explicitly"
        add(formSplitter);
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

    private void redrawForm() {
        formPanel.removeAll();

        JComponent rootWidget = rootComponent.createWidget(project, formEntity, selection, componentToWidget, new HashSet<>());
        widgetToComponent.clear();
        BaseUtils.reverse(componentToWidget, widgetToComponent);

        if (rootWidget != null) {
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

    @Override
    public void dispose() {
        // ignore
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
