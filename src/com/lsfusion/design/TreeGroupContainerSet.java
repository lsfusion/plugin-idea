package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.TreeGroupView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class TreeGroupContainerSet {
    public static final String TREE_GROUP_CONTAINER = ".box";

    private ContainerView treeContainer;
    private ContainerView controlsContainer;
    private ContainerView rightControlsContainer;
    private ContainerView filtersContainer;
    private ContainerView toolbarPropsContainer;

    public ContainerView getTreeContainer() {
        return treeContainer;
    }

    public ContainerView getControlsContainer() {
        return controlsContainer;
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

        set.treeContainer = factory.createContainer();
//        set.treeContainer.setCaption(getString("form.layout.tree"));
//        set.treeContainer.setDescription(getString("form.layout.tree"));
        set.treeContainer.setSID(treeGroup.getSID() + TREE_GROUP_CONTAINER);

        set.controlsContainer = factory.createContainer();
//        set.controlsContainer.setDescription(getString("form.layout.control.objects"));
        set.controlsContainer.setSID(treeGroup.getSID() + GroupObjectContainerSet.CONTROLS_CONTAINER);

        set.toolbarPropsContainer = factory.createContainer();
//        set.toolbarPropsContainer.setDescription(getString("form.layout.toolbar.props.container"));
        set.toolbarPropsContainer.setSID(treeGroup.getSID() + GroupObjectContainerSet.TOOLBAR_PROPS_CONTAINER);

        set.filtersContainer = factory.createContainer();
//        set.filtersContainer.setDescription(getString("form.layout.filters.container"));
        set.filtersContainer.setSID(treeGroup.getSID() + GroupObjectContainerSet.FILTERS_CONTAINER);

        set.rightControlsContainer = factory.createContainer();
        set.rightControlsContainer.setSID(treeGroup.getSID() + GroupObjectContainerSet.CONTROLS_RIGHT_CONTAINER);

        set.treeContainer.setType(ContainerType.CONTAINERV);
        set.treeContainer.setFlex(1);
        set.treeContainer.setAlignment(FlexAlignment.STRETCH);
        set.treeContainer.add(treeGroup);
        set.treeContainer.add(set.controlsContainer);
        set.treeContainer.add(treeGroup.filter);

        set.controlsContainer.setType(ContainerType.CONTAINERH);
        set.controlsContainer.setAlignment(FlexAlignment.STRETCH);
        set.controlsContainer.setChildrenAlignment(Alignment.LEADING);
        set.controlsContainer.add(treeGroup.toolbar);
        set.controlsContainer.add(set.rightControlsContainer);

        set.rightControlsContainer.setType(ContainerType.CONTAINERH);
        set.rightControlsContainer.setAlignment(FlexAlignment.CENTER);
        set.rightControlsContainer.setChildrenAlignment(Alignment.TRAILING);
        set.rightControlsContainer.add(set.filtersContainer);
        set.rightControlsContainer.add(set.toolbarPropsContainer);

        set.filtersContainer.setType(ContainerType.CONTAINERH);
        set.filtersContainer.setAlignment(FlexAlignment.CENTER);
        set.filtersContainer.setChildrenAlignment(Alignment.TRAILING);

        set.toolbarPropsContainer.setType(ContainerType.CONTAINERH);
        set.toolbarPropsContainer.setAlignment(FlexAlignment.CENTER);

        treeGroup.setFlex(1);
        treeGroup.setAlignment(FlexAlignment.STRETCH);

        treeGroup.filter.setAlignment(FlexAlignment.STRETCH);
        treeGroup.toolbar.setMargin(2);

        return set;
    }
}
