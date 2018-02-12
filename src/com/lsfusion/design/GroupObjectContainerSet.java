package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.GroupObjectView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class GroupObjectContainerSet {
    public static final String BOX_CONTAINER = "BOX";
        public static final String GRID_BOX_CONTAINER = "GRIDBOX";
            public static final String CLASSCHOOSER_COMPONENT = "CLASSCHOOSER"; // COMPONENT
            public static final String GRID_COMPONENT = "GRID"; // COMPONENT
        public static final String TOOLBARBOX_CONTAINER = "TOOLBARBOX";
            public static final String TOOLBARLEFT_CONTAINER = "TOOLBARLEFT";
                public static final String TOOLBAR_SYSTEM_COMPONENT = "TOOLBARSYSTEM"; // COMPONENT
            public static final String TOOLBARRIGHT_CONTAINER = "TOOLBARRIGHT";
                public static final String FILTERGROUPS_CONTAINER = "FILTERGROUPS";
                    public static final String FILTERGROUP_COMPONENT = "FILTERGROUP"; // COMPONENT
                public static final String TOOLBAR_CONTAINER = "TOOLBAR";
                public static final String SHOWTYPE_COMPONENT = "SHOWTYPE"; // COMPONENT
        public static final String USERFILTER_COMPONENT = "USERFILTER"; // COMPONENT
        public static final String PANEL_CONTAINER = "PANEL";
            public static final String GROUP_CONTAINER = "GROUP";

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

    public static GroupObjectContainerSet create(GroupObjectView group, DefaultFormView.ContainerFactory factory) {
        GroupObjectContainerSet set = new GroupObjectContainerSet();
        String sid = group.getPropertyGroupContainerSID();

        set.boxContainer = factory.createContainer();
        set.boxContainer.setCaption(group.getCaption());
//        set.groupContainer.setDescription(getString("form.layout.group.objects"));
        set.boxContainer.setSID(DefaultFormView.getBoxSID(sid));

        set.gridBoxContainer = factory.createContainer();
//        set.gridContainer.setDescription(getString("form.layout.grid.part"));
        set.gridBoxContainer.setSID(DefaultFormView.getGridBoxSID(sid));

        set.panelContainer = factory.createContainer();
//        set.panelContainer.setDescription(getString("form.layout.panel"));
        set.panelContainer.setSID(DefaultFormView.getPanelSID(sid));

        set.panelPropsContainer = factory.createContainer();
        set.panelPropsContainer.setSID(GROUP_CONTAINER + "(," + sid + ")");

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
        set.boxContainer.setChildrenAlignment(Alignment.START);
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
        set.controlsContainer.setChildrenAlignment(Alignment.START);
        set.controlsContainer.add(set.leftControlsContainer);
        set.controlsContainer.add(set.rightControlsContainer);

        set.leftControlsContainer.setType(ContainerType.CONTAINERH);
        set.leftControlsContainer.setAlignment(FlexAlignment.CENTER);
        set.leftControlsContainer.setChildrenAlignment(Alignment.END);
        set.leftControlsContainer.add(group.toolbar);

        set.rightControlsContainer.setType(ContainerType.CONTAINERH);
        set.rightControlsContainer.setAlignment(FlexAlignment.CENTER);
        set.rightControlsContainer.setChildrenAlignment(Alignment.END);
        set.rightControlsContainer.setFlex(1);
        set.rightControlsContainer.add(set.filtersContainer);
        set.rightControlsContainer.add(set.toolbarPropsContainer);
//        set.rightControlsContainer.add(group.showType);

        set.filtersContainer.setType(ContainerType.CONTAINERH);
        set.filtersContainer.setAlignment(FlexAlignment.CENTER);
        set.filtersContainer.setChildrenAlignment(Alignment.END);

        set.toolbarPropsContainer.setType(ContainerType.CONTAINERH);
        set.toolbarPropsContainer.setAlignment(FlexAlignment.CENTER);

        set.panelContainer.setType(ContainerType.CONTAINERV);
        set.panelContainer.setAlignment(FlexAlignment.STRETCH);
        set.panelContainer.setChildrenAlignment(Alignment.START);
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
