package com.lsfusion.design.ui;

import com.intellij.designer.propertyTable.PropertyTable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CheckboxTreeBase;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.design.model.*;
import com.lsfusion.design.vfs.LSFDesignVirtualFileImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static javax.swing.BorderFactory.*;

/*
    TODO:
    - восстанавливать состояние GUI при чеках в дереве
    - выключить по умолчанию
        - свойства, за исключением тех, которые FORCE PANEL
        - formEdit, formDrop, formOk, formClose
            - просто не делать им force panel при создании 
 */
public class EditorPanel extends JPanel {

    private final ContainerView rootComponent;
    @NotNull
    private final Project project;
    private ComponentTreeNode rootNode;
    private ComponentTree tree;
    private PropertyTable table;
    private JBCheckBox cbShowExpert;
    private JBSplitter topSplitter;
    
    private final Map<ComponentView, Boolean> selection = new HashMap<ComponentView, Boolean>();

    public EditorPanel(@NotNull Project project, @NotNull LSFDesignVirtualFileImpl file) {
        super(null);
        
        this.project = project;

        rootComponent = TestData.create();
        
        createLayout();
        initUiHandlers();
    }

    private void initUiHandlers() {
        cbShowExpert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.showExpert(cbShowExpert.isSelected());
            }
        });
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                List<ComponentView> selectedComponents = new ArrayList<ComponentView>();
                for (ComponentTreeNode node : tree.getSelectedNodes(ComponentTreeNode.class, null)) {
                    selectedComponents.add(node.getComponent());
                }
                table.update(selectedComponents, table.getSelectionProperty());
            }
        });
        
        tree.setCheckedListener(new CheckedListener() {
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
        cbShowExpert = new JBCheckBox("Show expert properties");
        
        JBSplitter treeAndTable = new JBSplitter(true);
        treeAndTable.setFirstComponent(createComponentTree());
        treeAndTable.setSecondComponent(createComponentPropertyTable());

        JBPanel leftPanel = new JBPanel(new BorderLayout());
        leftPanel.add(treeAndTable);
        leftPanel.add(cbShowExpert, BorderLayout.SOUTH);

        topSplitter = new JBSplitter(false, 0.25f);
        topSplitter.setFirstComponent(leftPanel);
        rebuildForm();
        
        setLayout(new BorderLayout());
        add(topSplitter);
    }

    private JComponent createComponentPropertyTable() {
        table = new ComponentPropertyTable();
        table.update(Arrays.asList(rootComponent), null);
        return new JBScrollPane(table);
    }

    private JComponent createComponentTree() {
        rootNode = createComponentNode(rootComponent);
        ComponentTreeCellRenderer renderer = new ComponentTreeCellRenderer();
        CheckboxTreeBase.CheckPolicy policy = new CheckboxTreeBase.CheckPolicy(true, true, false, false);
        tree = new ComponentTree(renderer, rootNode, policy);
        tree.expandRow(0);
        return new JBScrollPane(tree);
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
            if (!property.isForcedPanel) {
                return false;
            }
        }
        return true;
    }

    private void rebuildForm() {
        JBPanel formPanel = new JBPanel(new BorderLayout());
        JComponent rootWidget = rootComponent.createWidget(project, selection);
        if (rootWidget != null) {
            rootWidget.setBorder(
                    createCompoundBorder(
                            createCompoundBorder(
                                    createEmptyBorder(5, 5, 5, 5),
                                    createLineBorder(new Color(69, 160, 255), 1)
                            ),
                            createEmptyBorder(5, 5, 5, 5)
                    )
            );
            formPanel.add(rootWidget);
        }

        topSplitter.setSecondComponent(formPanel);
    }
}
