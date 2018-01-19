package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.TreeGroupView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class TreeGroupContainerSet {
    public static final String TREE_CONTAINER = "TREE";

    private ContainerView boxContainer;
    private ContainerView gridBoxContainer;
    private ContainerView panelContainer;
    private ContainerView panelPropsContainer;
    private ContainerView controlsContainer;
    private ContainerView leftControlsContainer;
    private ContainerView rightControlsContainer;
    private ContainerView filtersContainer;
    private ContainerView toolbarPropsContainer;

    public ContainerView getBoxContainer() {
        return boxContainer;
    }

    public ContainerView getGridBoxContainer() {
        return gridBoxContainer;
    }

    public ContainerView getPanelContainer() {
        return panelContainer;
    }

    public ContainerView getPanelPropsContainer() {
        return panelPropsContainer;
    }

    public ContainerView getControlsContainer() {
        return controlsContainer;
    }

    public ContainerView getLeftControlsContainer() {
        return leftControlsContainer;
    }

    public ContainerView getRightControlsContainer() {
        return rightControlsContainer;
    }

    public ContainerView getFiltersContainer() {
        return filtersContainer;
    }

    public ContainerView getToolbarPropsContainer() {
        return toolbarPropsContainer;
    }

    public static TreeGroupContainerSet create(TreeGroupView treeGroup, DefaultFormView.ContainerFactory factory) {
        TreeGroupContainerSet set = new TreeGroupContainerSet();
        String sid = treeGroup.getPropertyGroupContainerSID();

        set.boxContainer = factory.createContainer();
//        set.treeContainer.setCaption(getString("form.layout.tree"));
//        set.treeContainer.setDescription(getString("form.layout.tree"));
        set.boxContainer.setSID(DefaultFormView.getBoxSID(sid));

        set.gridBoxContainer = factory.createContainer();
//        set.gridContainer.setDescription(getString("form.layout.grid.part"));
        set.gridBoxContainer.setSID(DefaultFormView.getGridBoxSID(sid));

        set.panelContainer = factory.createContainer();
//        set.panelContainer.setDescription(getString("form.layout.panel"));
        set.panelContainer.setSID(DefaultFormView.getPanelSID(sid));

        set.panelPropsContainer = factory.createContainer();
        set.panelPropsContainer.setSID(GroupObjectContainerSet.GROUP_CONTAINER + "(," + sid + ")");
        
        set.controlsContainer = factory.createContainer();
//        set.controlsContainer.setDescription(getString("form.layout.control.objects"));
        set.controlsContainer.setSID(DefaultFormView.getToolbarBoxSID(sid));

        set.toolbarPropsContainer = factory.createContainer();
//        set.toolbarPropsContainer.setDescription(getString("form.layout.toolbar.props.container"));
        set.toolbarPropsContainer.setSID(DefaultFormView.getToolbarSID(sid));

        set.filtersContainer = factory.createContainer();
//        set.filtersContainer.setDescription(getString("form.layout.filters.container"));
        set.filtersContainer.setSID(DefaultFormView.getRegularFilterGroupsSID(sid));

        set.rightControlsContainer = factory.createContainer();
        set.rightControlsContainer.setSID(DefaultFormView.getToolbarRightSID(sid));

        set.leftControlsContainer = factory.createContainer();
        set.leftControlsContainer.setSID(DefaultFormView.getToolbarLeftSID(sid));

        set.boxContainer.setType(ContainerType.CONTAINERV);
        set.boxContainer.setChildrenAlignment(Alignment.LEADING);
        set.boxContainer.setAlignment(FlexAlignment.STRETCH);
        set.boxContainer.setFlex(1);
        set.boxContainer.add(set.gridBoxContainer);
        set.boxContainer.add(set.controlsContainer);
        set.boxContainer.add(treeGroup.filter);
        set.boxContainer.add(set.panelContainer);

        set.gridBoxContainer.setType(ContainerType.SPLITH);
        set.gridBoxContainer.setAlignment(FlexAlignment.STRETCH);
        set.gridBoxContainer.setFlex(1);
        set.gridBoxContainer.add(treeGroup);

        set.controlsContainer.setType(ContainerType.CONTAINERH);
        set.controlsContainer.setAlignment(FlexAlignment.STRETCH);
        set.controlsContainer.setChildrenAlignment(Alignment.LEADING);
        set.controlsContainer.add(set.leftControlsContainer);
        set.controlsContainer.add(set.rightControlsContainer);

        set.leftControlsContainer.setType(ContainerType.CONTAINERH);
        set.leftControlsContainer.setAlignment(FlexAlignment.CENTER);
        set.leftControlsContainer.setChildrenAlignment(Alignment.TRAILING);
        set.leftControlsContainer.add(treeGroup.toolbar);

        set.rightControlsContainer.setType(ContainerType.CONTAINERH);
        set.rightControlsContainer.setAlignment(FlexAlignment.CENTER);
        set.rightControlsContainer.setChildrenAlignment(Alignment.TRAILING);
        set.rightControlsContainer.setFlex(1);
        set.rightControlsContainer.add(set.filtersContainer);
        set.rightControlsContainer.add(set.toolbarPropsContainer);
        

        set.filtersContainer.setType(ContainerType.CONTAINERH);
        set.filtersContainer.setAlignment(FlexAlignment.CENTER);
        set.filtersContainer.setChildrenAlignment(Alignment.TRAILING);

        set.toolbarPropsContainer.setType(ContainerType.CONTAINERH);
        set.toolbarPropsContainer.setAlignment(FlexAlignment.CENTER);

        set.panelContainer.setType(ContainerType.CONTAINERV);
        set.panelContainer.setAlignment(FlexAlignment.STRETCH);
        set.panelContainer.setChildrenAlignment(Alignment.LEADING);
        set.panelContainer.add(set.panelPropsContainer);

        set.panelPropsContainer.setType(ContainerType.COLUMNS);
        set.panelPropsContainer.setColumns(4);

        treeGroup.filter.setAlignment(FlexAlignment.STRETCH);        
        treeGroup.toolbar.setAlignment(FlexAlignment.CENTER);
        treeGroup.toolbar.setMargin(2);
        
        
        

        return set;
    }
}
