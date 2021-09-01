package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.TreeGroupView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class TreeGroupContainerSet {
    public static final String TREE_PREFIX = "TREE";

    private ContainerView boxContainer;
    private ContainerView gridBoxContainer;
    private ContainerView panelContainer;
    private ContainerView groupContainer;
    private ContainerView toolbarBoxContainer;
    private ContainerView toolbarLeftContainer;
    private ContainerView toolbarRightContainer;
    private ContainerView filterGroupsContainer;
    private ContainerView toolbarContainer;

    public ContainerView getBoxContainer() {
        return boxContainer;
    }

    public ContainerView getGridBoxContainer() {
        return gridBoxContainer;
    }

    public ContainerView getPanelContainer() {
        return panelContainer;
    }

    public ContainerView getGroupContainer() {
        return groupContainer;
    }

    public ContainerView getToolbarBoxContainer() {
        return toolbarBoxContainer;
    }

    public ContainerView getToolbarLeftContainer() {
        return toolbarLeftContainer;
    }

    public ContainerView getToolbarRightContainer() {
        return toolbarRightContainer;
    }

    public ContainerView getFilterGroupsContainer() {
        return filterGroupsContainer;
    }

    public ContainerView getToolbarContainer() {
        return toolbarContainer;
    }

    public static TreeGroupContainerSet create(TreeGroupView treeGroup, DefaultFormView.ContainerFactory factory) {
        TreeGroupContainerSet set = new TreeGroupContainerSet();
        String sid = treeGroup.getPropertyGroupContainerSID();

        set.boxContainer = factory.createContainer();
//        set.treeContainer.setCaption(getString("form.layout.tree"));
//        set.treeContainer.setDescription(getString("form.layout.tree"));
        set.boxContainer.setSID(DefaultFormView.getBoxContainerSID(sid));

        set.gridBoxContainer = factory.createContainer();
//        set.gridContainer.setDescription(getString("form.layout.grid.part"));
        set.gridBoxContainer.setSID(DefaultFormView.getGridBoxContainerSID(sid));

        set.panelContainer = factory.createContainer();
//        set.panelContainer.setDescription(getString("form.layout.panel"));
        set.panelContainer.setSID(DefaultFormView.getPanelContainerSID(sid));

        set.groupContainer = factory.createContainer();
        set.groupContainer.setSID(DefaultFormView.getGOGroupContainerSID( "," + sid));
        
        set.toolbarBoxContainer = factory.createContainer();
//        set.toolbarBoxContainer.setDescription(getString("form.layout.control.objects"));
        set.toolbarBoxContainer.setSID(DefaultFormView.getToolbarBoxContainerSID(sid));

        set.toolbarContainer = factory.createContainer();
//        set.toolbarContainer.setDescription(getString("form.layout.toolbar.props.container"));
        set.toolbarContainer.setSID(DefaultFormView.getToolbarContainerSID(sid));

        set.filterGroupsContainer = factory.createContainer();
//        set.filterGroupsContainer.setDescription(getString("form.layout.filters.container"));
        set.filterGroupsContainer.setSID(DefaultFormView.getFilterGroupsContainerSID(sid));

        set.toolbarRightContainer = factory.createContainer();
        set.toolbarRightContainer.setSID(DefaultFormView.getToolbarRightContainerSID(sid));

        set.toolbarLeftContainer = factory.createContainer();
        set.toolbarLeftContainer.setSID(DefaultFormView.getToolbarLeftContainerSID(sid));

        set.boxContainer.setType(ContainerType.CONTAINERV);
        set.boxContainer.setChildrenAlignment(Alignment.START);
        set.boxContainer.setAlignment(FlexAlignment.STRETCH);
        set.boxContainer.setFlex(1);
        set.boxContainer.add(treeGroup.userFilter);
        set.boxContainer.add(set.gridBoxContainer);
        set.boxContainer.add(set.toolbarBoxContainer);
        set.boxContainer.add(set.panelContainer);

        set.gridBoxContainer.setType(ContainerType.SPLITH);
        set.gridBoxContainer.setAlignment(FlexAlignment.STRETCH);
        set.gridBoxContainer.setFlex(1);
        set.gridBoxContainer.add(treeGroup);

        set.toolbarBoxContainer.setType(ContainerType.CONTAINERH);
        set.toolbarBoxContainer.setAlignment(FlexAlignment.STRETCH);
        set.toolbarBoxContainer.setChildrenAlignment(Alignment.START);
        set.toolbarBoxContainer.add(set.toolbarLeftContainer);
        set.toolbarBoxContainer.add(set.toolbarRightContainer);

        set.toolbarLeftContainer.setType(ContainerType.CONTAINERH);
        set.toolbarLeftContainer.setAlignment(FlexAlignment.CENTER);
        set.toolbarLeftContainer.setChildrenAlignment(Alignment.END);
        set.toolbarLeftContainer.add(treeGroup.toolbarSystem);

        set.toolbarRightContainer.setType(ContainerType.CONTAINERH);
        set.toolbarRightContainer.setAlignment(FlexAlignment.CENTER);
        set.toolbarRightContainer.setChildrenAlignment(Alignment.END);
        set.toolbarRightContainer.setFlex(1);
        set.toolbarRightContainer.add(set.filterGroupsContainer);
        set.toolbarRightContainer.add(set.toolbarContainer);
        

        set.filterGroupsContainer.setType(ContainerType.CONTAINERH);
        set.filterGroupsContainer.setAlignment(FlexAlignment.CENTER);
        set.filterGroupsContainer.setChildrenAlignment(Alignment.END);

        set.toolbarContainer.setType(ContainerType.CONTAINERH);
        set.toolbarContainer.setAlignment(FlexAlignment.CENTER);

        set.panelContainer.setType(ContainerType.CONTAINERV);
        set.panelContainer.setAlignment(FlexAlignment.STRETCH);
        set.panelContainer.setChildrenAlignment(Alignment.START);
        set.panelContainer.add(set.groupContainer);

        set.groupContainer.setType(ContainerType.COLUMNS);
        set.groupContainer.setColumns(4);

        treeGroup.userFilter.setAlignment(FlexAlignment.STRETCH);        
        treeGroup.toolbarSystem.setAlignment(FlexAlignment.CENTER);
        treeGroup.toolbarSystem.setMargin(2);
        
        
        

        return set;
    }
}
