package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.GroupObjectView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class GroupObjectContainerSet {
    public static final String GROUP_CONTAINER = ".box";
    public static final String GRID_CONTAINER = ".grid.box";
    public static final String PANEL_CONTAINER = ".panel";
    public static final String PANEL_PROPS_CONTAINER = ".panel.props";
    public static final String FILTERS_CONTAINER = ".filters";
    public static final String TOOLBAR_PROPS_CONTAINER = ".toolbar.props.box";
    public static final String CONTROLS_CONTAINER = ".controls";
    public static final String CONTROLS_RIGHT_CONTAINER = ".controls.right";

    private ContainerView groupContainer;
    private ContainerView gridContainer;
    private ContainerView panelContainer;
    private ContainerView panelPropsContainer;
    private ContainerView controlsContainer;
    private ContainerView rightControlsContainer;
    private ContainerView filtersContainer;
    private ContainerView toolbarPropsContainer;

    public ContainerView getGroupContainer() {
        return groupContainer;
    }

    public ContainerView getGridContainer() {
        return gridContainer;
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

    public ContainerView getRightControlsContainer() {
        return rightControlsContainer;
    }

    public ContainerView getFiltersContainer() {
        return filtersContainer;
    }

    public ContainerView getToolbarPropsContainer() {
        return toolbarPropsContainer;
    }

    public static GroupObjectContainerSet create(GroupObjectView group, DefaultFormView.ContainerFactory factory) {

        GroupObjectContainerSet set = new GroupObjectContainerSet();

        set.groupContainer = factory.createContainer();
//        set.groupContainer.setCaption(group.getCaption());
//        set.groupContainer.setDescription(getString("form.layout.group.objects"));
        set.groupContainer.setSID(group.sID + GROUP_CONTAINER);

        set.gridContainer = factory.createContainer();
//        set.gridContainer.setDescription(getString("form.layout.grid.part"));
        set.gridContainer.setSID(group.sID + GRID_CONTAINER);

        set.panelContainer = factory.createContainer();
//        set.panelContainer.setDescription(getString("form.layout.panel"));
        set.panelContainer.setSID(group.sID + PANEL_CONTAINER);

        set.panelPropsContainer = factory.createContainer();
        set.panelPropsContainer.setSID(group.sID + PANEL_PROPS_CONTAINER);

        set.controlsContainer = factory.createContainer();
//        set.controlsContainer.setDescription(getString("form.layout.control.objects"));
        set.controlsContainer.setSID(group.sID + CONTROLS_CONTAINER);

        set.toolbarPropsContainer = factory.createContainer();
//        set.toolbarPropsContainer.setDescription(getString("form.layout.toolbar.props.container"));
        set.toolbarPropsContainer.setSID(group.sID + TOOLBAR_PROPS_CONTAINER);

        set.filtersContainer = factory.createContainer();
//        set.filtersContainer.setDescription(getString("form.layout.filters.container"));
        set.filtersContainer.setSID(group.sID + FILTERS_CONTAINER);

        set.rightControlsContainer = factory.createContainer();
        set.rightControlsContainer.setSID(group.sID + CONTROLS_RIGHT_CONTAINER);

        set.groupContainer.setType(ContainerType.CONTAINERV);
        set.groupContainer.setChildrenAlignment(Alignment.LEADING);
        set.groupContainer.setAlignment(FlexAlignment.STRETCH);
        set.groupContainer.setFlex(1);
        set.groupContainer.add(set.gridContainer);
        set.groupContainer.add(set.controlsContainer);
        set.groupContainer.add(group.filter);
        set.groupContainer.add(set.panelContainer);

        set.gridContainer.setType(ContainerType.SPLITH);
        set.gridContainer.setAlignment(FlexAlignment.STRETCH);
        set.gridContainer.setFlex(1);
        set.gridContainer.add(group.grid);

        set.controlsContainer.setType(ContainerType.CONTAINERH);
        set.controlsContainer.setAlignment(FlexAlignment.STRETCH);
        set.controlsContainer.setChildrenAlignment(Alignment.LEADING);
        set.controlsContainer.add(group.toolbar);
        set.controlsContainer.add(set.rightControlsContainer);

        set.rightControlsContainer.setType(ContainerType.CONTAINERH);
        set.rightControlsContainer.setAlignment(FlexAlignment.CENTER);
        set.rightControlsContainer.setChildrenAlignment(Alignment.TRAILING);
        set.rightControlsContainer.setFlex(1);
        set.rightControlsContainer.add(set.filtersContainer);
        set.rightControlsContainer.add(set.toolbarPropsContainer);
        set.rightControlsContainer.add(group.showType);

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

        group.toolbar.setAlignment(FlexAlignment.CENTER);
        group.toolbar.setMargin(2);
        group.showType.setAlignment(FlexAlignment.CENTER);
        group.showType.setMargin(2);

        return set;
    }
}
