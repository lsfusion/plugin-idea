package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class FormContainerSet {
    public static final String BOX_CONTAINER = "BOX";
    public static final String OBJECTS_CONTAINER = "OBJECTS";
    public static final String TOOLBARBOX_CONTAINER = "TOOLBARBOX";
    public static final String TOOLBARLEFT_CONTAINER = "TOOLBARLEFT";
    public static final String TOOLBARRIGHT_CONTAINER = "TOOLBARRIGHT";
    public static final String PANEL_CONTAINER = "PANEL";
    public static final String GROUP_CONTAINER = "GROUP";
    public static final String TOOLBAR_CONTAINER = "TOOLBAR";

    public static final String FUNCTIONSIN_CONTAINER = "functions.";

    private ContainerView mainContainer;
    private ContainerView objectsContainer;
    private ContainerView formButtonContainer;
    private ContainerView noGroupPanelContainer;
    private ContainerView noGroupPanelPropsContainer;
    private ContainerView noGroupToolbarPropsContainer;

    public ContainerView getMainContainer() {
        return mainContainer;
    }
    
    public ContainerView getObjectsContainer() {
        return objectsContainer;
    }

    public ContainerView getFormButtonContainer() {
        return formButtonContainer;
    }

    public ContainerView getNoGroupPanelContainer() {
        return noGroupPanelContainer;
    }

    public ContainerView getNoGroupPanelPropsContainer() {
        return noGroupPanelPropsContainer;
    }

    public ContainerView getNoGroupToolbarPropsContainer() {
        return noGroupToolbarPropsContainer;
    }

    public static FormContainerSet fillContainers(FormView form, FormView.ContainerFactory contFactory) {

        FormContainerSet set = new FormContainerSet();

        set.mainContainer = form.getMainContainer();
        set.mainContainer.setType(ContainerType.CONTAINERV);
//        set.mainContainer.setDescription(getString("form.layout.main.container"));
        
        set.objectsContainer = form.containerFactory.createContainer();
        set.objectsContainer.setSID(DefaultFormView.getObjectsSID());

        set.formButtonContainer = contFactory.createContainer();
//        set.formButtonContainer.setDescription(getString("form.layout.service.buttons"));
        set.formButtonContainer.setSID(DefaultFormView.getToolbarBoxSID());

        set.noGroupPanelContainer = contFactory.createContainer();
        set.noGroupPanelContainer.setSID(DefaultFormView.getPanelSID());

        set.noGroupPanelPropsContainer = contFactory.createContainer();
        set.noGroupPanelPropsContainer.setSID(DefaultFormView.getNoGroupObjectSID(""));

        set.mainContainer.setChildrenAlignment(Alignment.LEADING);
        set.mainContainer.setFlex(1);
        set.mainContainer.setAlignment(FlexAlignment.STRETCH);
        set.mainContainer.add(set.objectsContainer);
        set.mainContainer.add(set.noGroupPanelContainer);
        set.mainContainer.add(set.formButtonContainer);
        
        set.objectsContainer.setFlex(1);
        set.objectsContainer.setAlignment(FlexAlignment.STRETCH);

        set.formButtonContainer.setType(ContainerType.CONTAINERH);
        set.formButtonContainer.setAlignment(FlexAlignment.STRETCH);

        set.noGroupPanelContainer.setType(ContainerType.CONTAINERH);
        set.noGroupPanelContainer.setAlignment(FlexAlignment.STRETCH);
        set.noGroupPanelContainer.setChildrenAlignment(Alignment.LEADING);
        set.noGroupPanelContainer.add(set.noGroupPanelPropsContainer);

        set.noGroupPanelPropsContainer.setType(ContainerType.COLUMNS);
        set.noGroupPanelPropsContainer.setColumns(2);

        set.noGroupToolbarPropsContainer = contFactory.createContainer(); // контейнер тулбара
//        set.noGroupToolbarPropsContainer.setDescription(LocalizedString.create("{form.layout.toolbar.props.container}"));
        set.noGroupToolbarPropsContainer.setSID(DefaultFormView.getToolbarSID());

        set.noGroupToolbarPropsContainer.setType(ContainerType.CONTAINERH);
        set.noGroupToolbarPropsContainer.setAlignment(FlexAlignment.CENTER);

        return set;
    }
}
