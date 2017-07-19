package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.GroupObjectView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class GroupObjectContainerSet {
    public static final String GROUP_CONTAINER = ".box";
        public static final String GRID_BOX_CONTAINER = ".grid.box";
            public static final String CLASSCHOOSER_COMPONENT = ".classChooser"; // COMPONENT
            public static final String GRID_COMPONENT = ".grid"; // COMPONENT
        public static final String CONTROLS_CONTAINER = ".controls";
            public static final String TOOLBAR_COMPONENT = " .toolbar"; // COMPONENT
            public static final String CONTROLS_RIGHT_CONTAINER = ".controls.right";
                public static final String FILTERS_CONTAINER = ".filters";
                    public static final String FILTERSPREF_COMPONENT = "filters."; // COMPONENT
                public static final String TOOLBAR_PROPS_CONTAINER = ".toolbar.props.box";
                public static final String SHOWTYPE_COMPONENT = ".showType"; // COMPONENT
        public static final String FILTER_COMPONENT = ".filter"; // COMPONENT
        public static final String PANEL_CONTAINER = ".panel";
            public static final String PANEL_PROPS_CONTAINER = ".panel.props";

    private ContainerView boxContainer;
    private ContainerView gridBoxContainer;
    private ContainerView panelContainer;
    private ContainerView panelPropsContainer;
    private ContainerView controlsContainer;
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
        String sid = group.getPropertyGroupContainerSID();

        set.boxContainer = factory.createContainer();
        set.boxContainer.setCaption(group.getCaption());
//        set.groupContainer.setDescription(getString("form.layout.group.objects"));
        set.boxContainer.setSID(DefaultFormView.getGroupSID(sid));

        set.gridBoxContainer = factory.createContainer();
//        set.gridContainer.setDescription(getString("form.layout.grid.part"));
        set.gridBoxContainer.setSID(DefaultFormView.getGridBoxSID(sid));

        set.panelContainer = factory.createContainer();
//        set.panelContainer.setDescription(getString("form.layout.panel"));
        set.panelContainer.setSID(DefaultFormView.getPanelSID(sid));

        set.panelPropsContainer = factory.createContainer();
        set.panelPropsContainer.setSID(DefaultFormView.getPanelPropsSID(sid));

        set.controlsContainer = factory.createContainer();
//        set.controlsContainer.setDescription(getString("form.layout.control.objects"));
        set.controlsContainer.setSID(DefaultFormView.getControlsSID(sid));

        set.toolbarPropsContainer = factory.createContainer();
//        set.toolbarPropsContainer.setDescription(getString("form.layout.toolbar.props.container"));
        set.toolbarPropsContainer.setSID(DefaultFormView.getToolbarPropsSID(sid));

        set.filtersContainer = factory.createContainer();
//        set.filtersContainer.setDescription(getString("form.layout.filters.container"));
        set.filtersContainer.setSID(DefaultFormView.getFiltersSID(sid));

        set.rightControlsContainer = factory.createContainer();
        set.rightControlsContainer.setSID(DefaultFormView.getControlsRightSID(sid));

        set.boxContainer.setType(ContainerType.CONTAINERV);
        set.boxContainer.setChildrenAlignment(Alignment.LEADING);
        set.boxContainer.setAlignment(FlexAlignment.STRETCH);
        set.boxContainer.setFlex(1);
        set.boxContainer.add(set.gridBoxContainer);
        set.boxContainer.add(set.controlsContainer);
        set.boxContainer.add(group.filter);
        set.boxContainer.add(set.panelContainer);

        set.gridBoxContainer.setType(ContainerType.SPLITH);
        set.gridBoxContainer.setAlignment(FlexAlignment.STRETCH);
        set.gridBoxContainer.setFlex(1);
        set.gridBoxContainer.add(group.grid);

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
//        set.rightControlsContainer.add(group.showType);

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

        group.filter.setAlignment(FlexAlignment.STRETCH);
        group.toolbar.setAlignment(FlexAlignment.CENTER);
        group.toolbar.setMargin(2);
        
        group.showType.setAlignment(FlexAlignment.CENTER);
        group.showType.setMargin(2);

        return set;
    }
}
