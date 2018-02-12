package com.lsfusion.design;

import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lsfusion.design.model.*;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultFormView extends FormView {

    public ContainerView objectsContainer;
    public ContainerView formButtonContainer;
    public ContainerView noGroupPanelContainer;
    public ContainerView noGroupPanelPropsContainer;
    public ContainerView noGroupToolbarPropsContainer;

    public static DefaultFormView create(FormEntity entity) {
        DefaultFormView form = new DefaultFormView(entity);
        form.initProperties();
        return form;
    }

    protected DefaultFormView(FormEntity entity) {
        super(entity);

        caption = entity.title;

        FormContainerSet formSet = FormContainerSet.fillContainers(this, containerFactory);

        objectsContainer = formSet.getObjectsContainer();
        addComponentToMapping(objectsContainer);
        
        formButtonContainer = formSet.getFormButtonContainer();
        addComponentToMapping(formButtonContainer);
        
        noGroupPanelContainer = formSet.getNoGroupPanelContainer();
        addComponentToMapping(noGroupPanelContainer);
        panelContainers.put(null, noGroupPanelContainer);
        
        noGroupPanelPropsContainer = formSet.getNoGroupPanelPropsContainer();
        addComponentToMapping(noGroupPanelPropsContainer);
        panelPropsContainers.put(null, noGroupPanelPropsContainer);
        
        noGroupToolbarPropsContainer = formSet.getNoGroupToolbarPropsContainer();
        addComponentToMapping(noGroupToolbarPropsContainer);
        toolbarPropsContainers.put(null, noGroupToolbarPropsContainer);

        Iterator<TreeGroupView> treeIterator = treeGroups.iterator();
        TreeGroupView treeNextGroup = treeIterator.hasNext() ? treeIterator.next() : null;
        boolean groupStarted = false;
        for (GroupObjectView groupObject : groupObjects) {
            //если группа началась, ставим флаг. Если группа закончилась, addTreeGroupView и флаг снимаем
            if(treeNextGroup != null) {
                boolean isInTree = groupObject.entity.isInTree();
                if (isInTree && groupObject.entity.treeGroup.equals(treeNextGroup.entity)) {
                    groupStarted = true;
                } else if (groupStarted) {
                    addTreeGroupView(treeNextGroup);
                    treeNextGroup = treeIterator.hasNext() ? treeIterator.next() : null;
                    groupStarted = isInTree && treeNextGroup != null && groupObject.entity.treeGroup.equals(treeNextGroup.entity);
                }
            }
            addGroupObjectView(groupObject);
        }
        if(groupStarted)
            addTreeGroupView(treeNextGroup);

        for (RegularFilterGroupView filterGroup : regularFilters) {
            addRegularFilterGroupView(filterGroup);
        }
    }

    // SID BLOCK
    public static String getToolbarBoxSID(String goName) {
        return GroupObjectContainerSet.TOOLBARBOX_CONTAINER + "(" + goName + ")";
    }

    public static String getToolbarRightSID(String goName) {
        return GroupObjectContainerSet.TOOLBARRIGHT_CONTAINER + "(" + goName + ")";
    }
    
    public static String getToolbarLeftSID(String goName) {
        return GroupObjectContainerSet.TOOLBARLEFT_CONTAINER + "(" + goName + ")";
    }

    public static String getRegularFilterGroupsSID(String goName) {
        return GroupObjectContainerSet.FILTERGROUPS_CONTAINER + "(" + goName + ")";
    }

    public static String getGridBoxSID(String goName) {
        return GroupObjectContainerSet.GRID_BOX_CONTAINER + "(" + goName + ")";
    }

    public static String getBoxSID(String goName) {
        return GroupObjectContainerSet.BOX_CONTAINER + "(" + goName + ")";
    }

    public static String getPanelSID(String goName) {
        return GroupObjectContainerSet.PANEL_CONTAINER + "(" + goName + ")";
    }

    public static String getToolbarSID(String goName) {
        return GroupObjectContainerSet.TOOLBAR_CONTAINER + "(" + goName + ")";
    }

    public static String getNoGroupObjectSID(String pgName) {
        return FormContainerSet.GROUP_CONTAINER + "(" + pgName + ")";
    }

    public static String getObjectsSID() {
        return FormContainerSet.OBJECTS_CONTAINER;
    }

    public static String getToolbarBoxSID() {
        return FormContainerSet.TOOLBARBOX_CONTAINER;
    }

    public static String getToolbarLeftSID() {
        return FormContainerSet.TOOLBARLEFT_CONTAINER;
    }

    public static String getToolbarRightSID() {
        return FormContainerSet.TOOLBARRIGHT_CONTAINER;
    }

    public static String getPanelSID() {
        return FormContainerSet.PANEL_CONTAINER;
    }

    public static String getToolbarSID() {
        return FormContainerSet.TOOLBAR_CONTAINER;
    }
    
    public static String getPropertyGroupContainerSID(String groupObjectSID, String propertyGroupSID) {
        if(propertyGroupSID == null)
            propertyGroupSID = "";
        if(groupObjectSID == null)
            return getNoGroupObjectSID(propertyGroupSID);
        return GroupObjectContainerSet.GROUP_CONTAINER + "(" + propertyGroupSID + "," + groupObjectSID + ")";
    }

    private String getPropertyGroupContainerSID(PropertyDrawView propertyDraw, AbstractGroup propertyGroup) {
        String propertyGroupSID = propertyGroup.sID;
        if (propertyGroupSID.contains("_")) {
            String[] sids = propertyGroupSID.split("_", 2);
            propertyGroupSID = sids[1];
        }
        PropertyGroupContainerView propertyContainer = getPropertyContainer(propertyDraw);
        String containerSID = null;
        if(propertyContainer != null)
            containerSID = propertyContainer.getPropertyGroupContainerSID();
        
        return getPropertyGroupContainerSID(containerSID, propertyGroupSID);
    }

    private void initProperties() {
        for (PropertyDrawView propertyDraw : properties) {
            addPropertyDrawView(propertyDraw);
        }

        initFormButtons();
    }

    private PropertyGroupContainerView getPropertyContainer(PropertyDrawView property) {
        return getPropertyGroupContainer(property.entity.getToDraw(entity));
    }

    private PropertyGroupContainerView getPropertyGroupContainer(GroupObjectEntity groupObject) {
        if(groupObject == null)
            return null;
        if (groupObject.isInTree())
            return get(groupObject.treeGroup);
        return get(groupObject);
    }

    protected Map<PropertyGroupContainerView, ContainerView> boxContainers = new HashMap<>();
    protected Map<PropertyGroupContainerView, ContainerView> gridBoxContainers = new HashMap<>();

    protected Map<PropertyGroupContainerView, ContainerView> panelContainers = new HashMap<>();

    public ContainerView getPanelContainer(PropertyDrawView propertyDraw) {
        return panelContainers.get(getPropertyContainer(propertyDraw));
    }

    protected final Map<PropertyGroupContainerView, ContainerView> panelPropsContainers = new HashMap<>();

    public ContainerView getPanelPropsContainer(PropertyDrawView propertyDraw) {
        return panelPropsContainers.get(getPropertyContainer(propertyDraw));
    }

    protected transient Map<PropertyGroupContainerView, ContainerView> controlsContainers = new HashMap<>();

    public ContainerView getControlsContainer(GroupObjectView groupObject) {
        return controlsContainers.get(groupObject);
    }

    public ContainerView getControlsContainer(TreeGroupView treeGroup) {
        return controlsContainers.get(treeGroup);
    }

    protected final Map<PropertyGroupContainerView, ContainerView> toolbarPropsContainers = new HashMap<>();

    public ContainerView getToolbarPropsContainer(PropertyDrawView propertyDraw) {
        return toolbarPropsContainers.get(getPropertyContainer(propertyDraw));
    }

    protected transient Map<PropertyGroupContainerView, ContainerView> leftControlsContainers = new HashMap<>();
    protected transient Map<PropertyGroupContainerView, ContainerView> rightControlsContainers = new HashMap<>();

    public ContainerView getRightControlsContainer(GroupObjectView groupObject) {
        return rightControlsContainers.get(groupObject);
    }

    public ContainerView getRightControlsContainer(TreeGroupView treeGroup) {
        return rightControlsContainers.get(treeGroup);
    }

    protected final Map<PropertyGroupContainerView, ContainerView> filtersContainers = new HashMap<>();

    public ContainerView getFilterContainer(GroupObjectEntity group) {
        return filtersContainers.get(getPropertyGroupContainer(group));
    }

    protected transient Table<Optional<PropertyGroupContainerView>, AbstractGroup, ContainerView> groupPropertyContainers = HashBasedTable.create();

    public void addGroupObjectView(GroupObjectView goView) {
        GroupObjectContainerSet set = GroupObjectContainerSet.create(goView, containerFactory);

        objectsContainer.add(set.getBoxContainer());

        registerComponent(set.getBoxContainer(), boxContainers, goView);
        registerComponent(set.getGridBoxContainer(), gridBoxContainers, goView);
        registerComponent(set.getPanelContainer(), panelContainers, goView);
        registerComponent(set.getPanelPropsContainer(), panelPropsContainers, goView);
        registerComponent(set.getControlsContainer(), controlsContainers, goView);
        registerComponent(set.getLeftControlsContainer(), leftControlsContainers, goView);
        registerComponent(set.getRightControlsContainer(), rightControlsContainers, goView);
        registerComponent(set.getFiltersContainer(), filtersContainers, goView);
        registerComponent(set.getToolbarPropsContainer(), toolbarPropsContainers, goView);

        //todo classChoosers

        if (goView.entity.isFixedPanel()) {
            set.getBoxContainer().setFlex(0);
        }
    }

    public void registerComponent(ContainerView groupContainer, Map<PropertyGroupContainerView, ContainerView> boxContainers, PropertyGroupContainerView goView) {
        boxContainers.put(goView, groupContainer);
        addComponentToMapping(groupContainer);
    }

    public void addTreeGroupView(TreeGroupView treeGroup) {
        TreeGroupContainerSet treeSet = TreeGroupContainerSet.create(treeGroup, containerFactory);

        registerComponent(treeSet.getBoxContainer(), boxContainers, treeGroup);
        registerComponent(treeSet.getGridBoxContainer(), gridBoxContainers, treeGroup);
        registerComponent(treeSet.getPanelContainer(), panelContainers, treeGroup);
        registerComponent(treeSet.getPanelPropsContainer(), panelPropsContainers, treeGroup);
        registerComponent(treeSet.getControlsContainer(), controlsContainers, treeGroup);
        registerComponent(treeSet.getLeftControlsContainer(), leftControlsContainers, treeGroup);
        registerComponent(treeSet.getRightControlsContainer(), rightControlsContainers, treeGroup);
        registerComponent(treeSet.getFiltersContainer(), filtersContainers, treeGroup);
        registerComponent(treeSet.getToolbarPropsContainer(), toolbarPropsContainers, treeGroup);

        //вставляем перед первым groupObject в данной treeGroup
        objectsContainer.addBefore(treeSet.getBoxContainer(), boxContainers.get(mgroupObjects.get(treeGroup.entity.groups.get(0))));
    }

    public void addPropertyDrawView(PropertyDrawView propertyDraw) {
        if(!addGridDrawView(propertyDraw)) {
            addPanelPropertyDrawView(propertyDraw);
        }
    }

    public void addPanelPropertyDrawView(PropertyDrawView propertyDraw) {
        ContainerView propertyContainer;
        if (propertyDraw.entity.isToolbar(entity)) {
            propertyContainer = getToolbarPropsContainer(propertyDraw);
            propertyDraw.setAlignment(FlexAlignment.CENTER);
        } else {
            propertyContainer = getPropGroupContainer(propertyDraw, propertyDraw.entity.parent);
        }
        propertyContainer.add(propertyDraw);
    }

    private ContainerView getPropGroupContainer(PropertyDrawView propertyDraw, AbstractGroup currentGroup) {
        if (currentGroup == null) {
            return getPanelPropsContainer(propertyDraw);
        }

        if (!currentGroup.createContainer) {
            return getPropGroupContainer(propertyDraw, currentGroup.parent);
        }

        PropertyGroupContainerView propertyContainer = getPropertyContainer(propertyDraw);
        //ищем в созданных
        ContainerView currentGroupContainer = groupPropertyContainers.get(Optional.fromNullable(propertyContainer), currentGroup);
        if (currentGroupContainer == null) {
            String currentGroupContainerSID = getPropertyGroupContainerSID(propertyDraw, currentGroup);

            //ищем по имени
            currentGroupContainer = getContainerBySID(currentGroupContainerSID);
            if (currentGroupContainer == null) {
                //сначала создаём контейнеры для верхних групп, чтобы соблюдался порядок
                getPropGroupContainer(propertyDraw, currentGroup.parent);

                //затем создаём контейнер для текущей группы
                currentGroupContainer = createContainer(currentGroup.caption, currentGroupContainerSID);
                currentGroupContainer.setType(ContainerType.COLUMNS);
                currentGroupContainer.columns = 4;

                getPanelContainer(propertyDraw).add(currentGroupContainer);
            }

            groupPropertyContainers.put(Optional.fromNullable(propertyContainer), currentGroup, currentGroupContainer);
        }

        return currentGroupContainer;
    }

    private void addRegularFilterGroupView(RegularFilterGroupView filterGroup) {
        ContainerView filterContainer;
        filterContainer = getFilterContainer(filterGroup.entity.getToDraw(entity));
        if (filterContainer != null) {
            filterContainer.add(filterGroup);
        }
    }

    private void initFormButtons() {
//        PropertyDrawView printFunction = get(entity.printActionPropertyDraw);
//        setupFormButton(printFunction, KeyStrokes.getPrintKeyStroke(), "print.png", false);

//        PropertyDrawView xlsFunction = get(entity.xlsActionPropertyDraw);
//        setupFormButton(xlsFunction, KeyStrokes.getXlsKeyStroke(), "xls.png", false);

        PropertyDrawView editFunction = get(entity.editActionPropertyDraw);
        setupFormButton(editFunction, KeyStrokes.getEditKeyStroke(), "editReport.png", false);

        PropertyDrawView dropFunction = get(entity.dropActionPropertyDraw);
        setupFormButton(dropFunction, KeyStrokes.getNullKeyStroke(), null, true);

        PropertyDrawView refreshFunction = get(entity.refreshActionPropertyDraw);
        setupFormButton(refreshFunction, KeyStrokes.getRefreshKeyStroke(), "refresh.png", false);

        PropertyDrawView applyFunction = get(entity.applyActionPropertyDraw);
        setupFormButton(applyFunction, KeyStrokes.getApplyKeyStroke(), null, true);

        PropertyDrawView cancelFunction = get(entity.cancelActionPropertyDraw);
        setupFormButton(cancelFunction, KeyStrokes.getCancelKeyStroke(), null, true);

        PropertyDrawView okFunction = get(entity.okActionPropertyDraw);
        setupFormButton(okFunction, KeyStrokes.getOkKeyStroke(), null, true);

        PropertyDrawView closeFunction = get(entity.closeActionPropertyDraw);
        setupFormButton(closeFunction, KeyStrokes.getCloseKeyStroke(), null, true);

        ContainerView leftControlsContainer = createContainer(null, getToolbarLeftSID());
        leftControlsContainer.setType(ContainerType.CONTAINERH);
        leftControlsContainer.childrenAlignment = Alignment.START;
        leftControlsContainer.setFlex(0);

        ContainerView rightControlsContainer = createContainer(null, getToolbarRightSID());
        rightControlsContainer.setType(ContainerType.CONTAINERH);
        rightControlsContainer.childrenAlignment = Alignment.END;
        rightControlsContainer.setFlex(1);

//        leftControlsContainer.add(printFunction);
//        leftControlsContainer.add(xlsFunction);
        leftControlsContainer.add(editFunction);
        leftControlsContainer.add(dropFunction);

        rightControlsContainer.add(noGroupToolbarPropsContainer);
        rightControlsContainer.add(refreshFunction);
        rightControlsContainer.add(applyFunction);
        rightControlsContainer.add(cancelFunction);
        rightControlsContainer.add(okFunction);
        rightControlsContainer.add(closeFunction);

        formButtonContainer.add(leftControlsContainer);
        formButtonContainer.add(rightControlsContainer);

        if (!entity.isModal()) {
            removeComponent(okFunction);
            removeComponent(closeFunction);
        }
        if (!entity.isDialog()) {
            removeComponent(dropFunction);
        }
    }

    private void setupFormButton(PropertyDrawView action, KeyStroke editKey, String iconPath, boolean showCaption) {
        action.changeKey = editKey;
        action.focusable = false;
        action.setAlignment(FlexAlignment.STRETCH);
        action.imagePath = iconPath;
        action.showCaption = showCaption;

        if (iconPath != null) {
            action.showChangeKey = false;
        }
    }
}
