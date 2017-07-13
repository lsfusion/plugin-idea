package com.lsfusion.design;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class FormContainerSet {
    public static final String MAIN_CONTAINER = "main";
    public static final String FUNCTIONS_CONTAINER = "functions.box";
    public static final String LEFTCONTROLS_CONTAINER = "leftControls";
    public static final String RIGHTCONTROLS_CONTAINER = "rightControls";
    public static final String NOGROUP_PANEL_CONTAINER = "nogroup.panel";
    public static final String NOGROUP_PANEL_PROPS_CONTAINER = "nogroup.panel.props";

    public static final String FUNCTIONSIN_CONTAINER = "functions.";

    private ContainerView mainContainer;
    private ContainerView formButtonContainer;
    private ContainerView noGroupPanelContainer;
    private ContainerView noGroupPanelPropsContainer;

    public ContainerView getMainContainer() {
        return mainContainer;
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

    public static FormContainerSet fillContainers(FormView form, FormView.ContainerFactory contFactory) {

        FormContainerSet set = new FormContainerSet();

        set.mainContainer = form.getMainContainer();
        set.mainContainer.setType(ContainerType.CONTAINERV);
//        set.mainContainer.setDescription(getString("form.layout.main.container"));

        set.formButtonContainer = contFactory.createContainer();
//        set.formButtonContainer.setDescription(getString("form.layout.service.buttons"));
        set.formButtonContainer.setSID(FUNCTIONS_CONTAINER);

        set.noGroupPanelContainer = contFactory.createContainer();
        set.noGroupPanelContainer.setSID(NOGROUP_PANEL_CONTAINER);

        set.noGroupPanelPropsContainer = contFactory.createContainer();
        set.noGroupPanelPropsContainer.setSID(NOGROUP_PANEL_PROPS_CONTAINER);

        set.mainContainer.setChildrenAlignment(Alignment.LEADING);
        set.mainContainer.setFlex(1);
        set.mainContainer.setAlignment(FlexAlignment.STRETCH);
        set.mainContainer.add(set.noGroupPanelContainer);
        set.mainContainer.add(set.formButtonContainer);

        set.formButtonContainer.setType(ContainerType.CONTAINERH);
        set.formButtonContainer.setAlignment(FlexAlignment.STRETCH);

        set.noGroupPanelContainer.setType(ContainerType.CONTAINERH);
        set.noGroupPanelContainer.setAlignment(FlexAlignment.STRETCH);
        set.noGroupPanelContainer.setChildrenAlignment(Alignment.LEADING);
        set.noGroupPanelContainer.add(set.noGroupPanelPropsContainer);

        set.noGroupPanelPropsContainer.setType(ContainerType.COLUMNS);
        set.noGroupPanelPropsContainer.setColumns(2);

        return set;
    }
}
