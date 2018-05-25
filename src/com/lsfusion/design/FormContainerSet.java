package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class FormContainerSet {
    public static final String BOX_CONTAINER = "BOX";
        public static final String OBJECTS_CONTAINER = "OBJECTS";
        public static final String PANEL_CONTAINER = "PANEL";
            public static final String GROUP_CONTAINER = "GROUP";
        public static final String TOOLBARBOX_CONTAINER = "TOOLBARBOX";
            public static final String TOOLBARLEFT_CONTAINER = "TOOLBARLEFT";
            public static final String TOOLBARRIGHT_CONTAINER = "TOOLBARRIGHT";
                public static final String TOOLBAR_CONTAINER = "TOOLBAR";

    private ContainerView mainContainer;
    private ContainerView objectsContainer;
    private ContainerView toolbarBoxContainer;
    private ContainerView panelContainer;
    private ContainerView groupContainer;
    private ContainerView toolbarContainer;

    public ContainerView getMainContainer() {
        return mainContainer;
    }
    
    public ContainerView getObjectsContainer() {
        return objectsContainer;
    }

    public ContainerView getToolbarBoxContainer() {
        return toolbarBoxContainer;
    }

    public ContainerView getPanelContainer() {
        return panelContainer;
    }

    public ContainerView getGroupContainer() {
        return groupContainer;
    }

    public ContainerView getToolbarContainer() {
        return toolbarContainer;
    }

    public static FormContainerSet fillContainers(FormView form, FormView.ContainerFactory contFactory) {

        FormContainerSet set = new FormContainerSet();

        set.mainContainer = form.getMainContainer();
        set.mainContainer.setType(ContainerType.CONTAINERV);
//        set.mainContainer.setDescription(getString("form.layout.main.container"));
        
        set.objectsContainer = form.containerFactory.createContainer();
        set.objectsContainer.setSID(DefaultFormView.getObjectsContainerSID());

        set.toolbarBoxContainer = contFactory.createContainer();
//        set.toolbarBoxContainer.setDescription(getString("form.layout.service.buttons"));
        set.toolbarBoxContainer.setSID(DefaultFormView.getToolbarBoxContainerSID());

        set.panelContainer = contFactory.createContainer();
        set.panelContainer.setSID(DefaultFormView.getPanelContainerSID());

        set.groupContainer = contFactory.createContainer();
        set.groupContainer.setSID(DefaultFormView.getGroupContainerSID(""));

        set.mainContainer.setChildrenAlignment(Alignment.START);
        set.mainContainer.setFlex(1);
        set.mainContainer.setAlignment(FlexAlignment.STRETCH);
        set.mainContainer.add(set.panelContainer);
        set.mainContainer.add(set.objectsContainer);
        set.mainContainer.add(set.toolbarBoxContainer);
        
        set.objectsContainer.setFlex(1);
        set.objectsContainer.setAlignment(FlexAlignment.STRETCH);

        set.toolbarBoxContainer.setType(ContainerType.CONTAINERH);
        set.toolbarBoxContainer.setAlignment(FlexAlignment.STRETCH);

        set.panelContainer.setType(ContainerType.CONTAINERH);
        set.panelContainer.setAlignment(FlexAlignment.STRETCH);
        set.panelContainer.setChildrenAlignment(Alignment.START);
        set.panelContainer.add(set.groupContainer);

        set.groupContainer.setType(ContainerType.COLUMNS);
        set.groupContainer.setColumns(2);

        set.toolbarContainer = contFactory.createContainer(); // контейнер тулбара
//        set.toolbarContainer.setDescription(LocalizedString.create("{form.layout.toolbar.props.container}"));
        set.toolbarContainer.setSID(DefaultFormView.getToolbarContainerSID());

        set.toolbarContainer.setType(ContainerType.CONTAINERH);
        set.toolbarContainer.setAlignment(FlexAlignment.CENTER);

        return set;
    }
}
