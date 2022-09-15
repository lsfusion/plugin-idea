package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.GroupObjectView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class GroupObjectContainerSet {
    public static final String BOX_CONTAINER = "BOX";
        public static final String FILTERBOX_CONTAINER = "FILTERBOX";
            public static final String FILTERS_CONTAINER = "FILTERS";
            public static final String FILTERCONTROLS_COMPONENT = "FILTERCONTROLS";
        public static final String CLASSCHOOSER_COMPONENT = "CLASSCHOOSER"; // COMPONENT
        public static final String GRID_COMPONENT = "GRID"; // COMPONENT
        public static final String TOOLBARBOX_CONTAINER = "TOOLBARBOX";
            public static final String TOOLBARLEFT_CONTAINER = "TOOLBARLEFT";
                public static final String TOOLBAR_SYSTEM_COMPONENT = "TOOLBARSYSTEM"; // COMPONENT
            public static final String TOOLBARRIGHT_CONTAINER = "TOOLBARRIGHT";
                public static final String FILTERGROUPS_CONTAINER = "FILTERGROUPS";
                    public static final String FILTERGROUP_COMPONENT = "FILTERGROUP"; // COMPONENT
                public static final String TOOLBAR_CONTAINER = "TOOLBAR";
        public static final String PANEL_CONTAINER = "PANEL";
            public static final String GROUP_CONTAINER = "GROUP";

    private ContainerView boxContainer;
    private ContainerView filterBoxContainer;
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
    
    public ContainerView getFilterBoxContainer() {
        return filterBoxContainer;
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

    public static GroupObjectContainerSet create(GroupObjectView group, DefaultFormView.ContainerFactory factory) {
        GroupObjectContainerSet set = new GroupObjectContainerSet();
        String sid = group.getPropertyGroupContainerSID();

        set.boxContainer = factory.createContainer();
        set.boxContainer.setCaption(group.getCaption());
//        set.groupContainer.setDescription(getString("form.layout.group.objects"));
        set.boxContainer.setSID(DefaultFormView.getBoxContainerSID(sid));

        set.filterBoxContainer = factory.createContainer();
        set.filterBoxContainer.setSID(DefaultFormView.getFilterBoxContainerSID(sid));

        set.panelContainer = factory.createContainer();
//        set.panelContainer.setDescription(getString("form.layout.panel"));
        set.panelContainer.setSID(DefaultFormView.getPanelContainerSID(sid));

        set.groupContainer = factory.createContainer();
        set.groupContainer.setSID(DefaultFormView.getGOGroupContainerSID("," + sid));

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
        set.boxContainer.add(set.filterBoxContainer);
        set.boxContainer.add(group.grid);
        set.boxContainer.add(set.toolbarBoxContainer);
        set.boxContainer.add(set.panelContainer);

        set.filterBoxContainer.setType(ContainerType.CONTAINERH);
        set.filterBoxContainer.add(group.filtersContainer);
        set.filterBoxContainer.add(group.filterControls);

        set.toolbarBoxContainer.setType(ContainerType.CONTAINERH);
        set.toolbarBoxContainer.setAlignment(FlexAlignment.STRETCH);
        set.toolbarBoxContainer.setChildrenAlignment(Alignment.START);
        set.toolbarBoxContainer.add(set.toolbarLeftContainer);
        set.toolbarBoxContainer.add(set.toolbarRightContainer);

        set.toolbarLeftContainer.setType(ContainerType.CONTAINERH);
        set.toolbarLeftContainer.setAlignment(FlexAlignment.CENTER);
        set.toolbarLeftContainer.setChildrenAlignment(Alignment.END);
        set.toolbarLeftContainer.add(group.toolbarSystem);

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
        set.groupContainer.setLines(4);

        group.toolbarSystem.setAlignment(FlexAlignment.CENTER);
        group.toolbarSystem.setMargin(2);

        return set;
    }
}
