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
    public ContainerView toolbarBoxContainer;
    public ContainerView panelContainer;
    public ContainerView groupContainer;
    public ContainerView toolbarContainer;

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
        
        toolbarBoxContainer = formSet.getToolbarBoxContainer();
        addComponentToMapping(toolbarBoxContainer);
        
        panelContainer = formSet.getPanelContainer();
        addComponentToMapping(panelContainer);
        panelContainers.put(null, panelContainer);
        
        groupContainer = formSet.getGroupContainer();
        addComponentToMapping(groupContainer);
        groupContainers.put(null, groupContainer);
        
        toolbarContainer = formSet.getToolbarContainer();
        addComponentToMapping(toolbarContainer);
        toolbarContainers.put(null, toolbarContainer);

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
    public static String getToolbarBoxContainerSID(String goName) {
        return GroupObjectContainerSet.TOOLBARBOX_CONTAINER + "(" + goName + ")";
    }

    public static String getToolbarRightContainerSID(String goName) {
        return GroupObjectContainerSet.TOOLBARRIGHT_CONTAINER + "(" + goName + ")";
    }
    
    public static String getToolbarLeftContainerSID(String goName) {
        return GroupObjectContainerSet.TOOLBARLEFT_CONTAINER + "(" + goName + ")";
    }

    public static String getFilterGroupsContainerSID(String goName) {
        return GroupObjectContainerSet.FILTERGROUPS_CONTAINER + "(" + goName + ")";
    }

    public static String getBoxContainerSID(String goName) {
        return GroupObjectContainerSet.BOX_CONTAINER + "(" + goName + ")";
    }
    
    public static String getFilterBoxContainerSID(String goName) {
        return GroupObjectContainerSet.FILTERBOX_CONTAINER + "(" + goName + ")";
    }
    
    public static String getPanelContainerSID(String goName) {
        return GroupObjectContainerSet.PANEL_CONTAINER + "(" + goName + ")";
    }

    public static String getToolbarContainerSID(String goName) {
        return GroupObjectContainerSet.TOOLBAR_CONTAINER + "(" + goName + ")";
    }

    public static String getGOGroupContainerSID(String goName) {
        return GroupObjectContainerSet.GROUP_CONTAINER + "(" + goName + ")";
    }

    public static String getGroupContainerSID(String pgName) {
        return FormContainerSet.GROUP_CONTAINER + "(" + pgName + ")";
    }

    public static String getObjectsContainerSID() {
        return FormContainerSet.OBJECTS_CONTAINER;
    }

    public static String getToolbarBoxContainerSID() {
        return FormContainerSet.TOOLBARBOX_CONTAINER;
    }

    public static String getToolbarLeftContainerSID() {
        return FormContainerSet.TOOLBARLEFT_CONTAINER;
    }

    public static String getToolbarRightContainerSID() {
        return FormContainerSet.TOOLBARRIGHT_CONTAINER;
    }

    public static String getPanelContainerSID() {
        return FormContainerSet.PANEL_CONTAINER;
    }

    public static String getToolbarContainerSID() {
        return FormContainerSet.TOOLBAR_CONTAINER;
    }
    
    public static String getPropertyGroupContainerSID(String groupObjectSID, String propertyGroupSID) {
        if(propertyGroupSID == null)
            propertyGroupSID = "";
        if(groupObjectSID == null)
            return getGroupContainerSID(propertyGroupSID);
        return getGOGroupContainerSID(propertyGroupSID + "," + groupObjectSID);
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
    
    protected Map<PropertyGroupContainerView, ContainerView> filtersContainers = new HashMap<>(); 
    protected Map<PropertyGroupContainerView, ContainerView> filterBoxContainers = new HashMap<>(); 

    protected Map<PropertyGroupContainerView, ContainerView> panelContainers = new HashMap<>();

    public ContainerView getPanelContainer(PropertyDrawView propertyDraw) {
        return panelContainers.get(getPropertyContainer(propertyDraw));
    }

    protected final Map<PropertyGroupContainerView, ContainerView> groupContainers = new HashMap<>();

    public ContainerView getPanelPropsContainer(PropertyDrawView propertyDraw) {
        return groupContainers.get(getPropertyContainer(propertyDraw));
    }

    protected transient Map<PropertyGroupContainerView, ContainerView> toolbarBoxContainers = new HashMap<>();

    protected final Map<PropertyGroupContainerView, ContainerView> toolbarContainers = new HashMap<>();

    public ContainerView getToolbarPropsContainer(PropertyDrawView propertyDraw) {
        return toolbarContainers.get(getPropertyContainer(propertyDraw));
    }

    protected transient Map<PropertyGroupContainerView, ContainerView> toolbarLeftContainers = new HashMap<>();
    protected transient Map<PropertyGroupContainerView, ContainerView> toolbarRightContainers = new HashMap<>();

    protected final Map<PropertyGroupContainerView, ContainerView> filterGroupsContainers = new HashMap<>();

    public ContainerView getFilterContainer(GroupObjectEntity group) {
        return filterGroupsContainers.get(getPropertyGroupContainer(group));
    }

    protected transient Table<Optional<PropertyGroupContainerView>, AbstractGroup, ContainerView> groupPropertyContainers = HashBasedTable.create();

    public void addGroupObjectView(GroupObjectView goView) {
        GroupObjectContainerSet set = GroupObjectContainerSet.create(goView, containerFactory);

        objectsContainer.add(set.getBoxContainer());

        registerComponent(set.getBoxContainer(), boxContainers, goView);
        registerComponent(set.getFilterBoxContainer(), filterBoxContainers, goView);
        registerComponent(goView.getFiltersContainer(), filtersContainers, goView);
        registerComponent(set.getPanelContainer(), panelContainers, goView);
        registerComponent(set.getGroupContainer(), groupContainers, goView);
        registerComponent(set.getToolbarBoxContainer(), toolbarBoxContainers, goView);
        registerComponent(set.getToolbarLeftContainer(), toolbarLeftContainers, goView);
        registerComponent(set.getToolbarRightContainer(), toolbarRightContainers, goView);
        registerComponent(set.getFilterGroupsContainer(), filterGroupsContainers, goView);
        registerComponent(set.getToolbarContainer(), toolbarContainers, goView);

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
        registerComponent(treeSet.getFilterBoxContainer(), filterBoxContainers, treeGroup);
        registerComponent(treeGroup.getFiltersContainer(), filtersContainers, treeGroup);
        registerComponent(treeSet.getPanelContainer(), panelContainers, treeGroup);
        registerComponent(treeSet.getGroupContainer(), groupContainers, treeGroup);
        registerComponent(treeSet.getToolbarBoxContainer(), toolbarBoxContainers, treeGroup);
        registerComponent(treeSet.getToolbarLeftContainer(), toolbarLeftContainers, treeGroup);
        registerComponent(treeSet.getToolbarRightContainer(), toolbarRightContainers, treeGroup);
        registerComponent(treeSet.getFilterGroupsContainer(), filterGroupsContainers, treeGroup);
        registerComponent(treeSet.getToolbarContainer(), toolbarContainers, treeGroup);

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
                currentGroupContainer.lines = 4;

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

        ContainerView toolbarLeftContainer = createContainer(null, getToolbarLeftContainerSID());
        toolbarLeftContainer.setType(ContainerType.CONTAINERH);
        toolbarLeftContainer.childrenAlignment = Alignment.START;
        toolbarLeftContainer.setFlex(0);

        ContainerView toolbarRightContainer = createContainer(null, getToolbarRightContainerSID());
        toolbarRightContainer.setType(ContainerType.CONTAINERH);
        toolbarRightContainer.childrenAlignment = Alignment.END;
        toolbarRightContainer.setFlex(1);

        toolbarLeftContainer.add(editFunction);

        toolbarRightContainer.add(toolbarContainer);
        toolbarRightContainer.add(refreshFunction);
        toolbarLeftContainer.add(dropFunction);
        toolbarRightContainer.add(applyFunction);
        toolbarRightContainer.add(cancelFunction);
        toolbarRightContainer.add(okFunction);
        toolbarRightContainer.add(closeFunction);

        toolbarBoxContainer.add(toolbarLeftContainer);
        toolbarBoxContainer.add(toolbarRightContainer);

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
