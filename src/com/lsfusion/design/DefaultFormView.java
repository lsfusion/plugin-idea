package com.lsfusion.design;

import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lsfusion.design.model.*;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.design.ui.FlexAlignment;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class DefaultFormView extends FormView {

    public ContainerView formButtonContainer;
    public ContainerView noGroupPanelContainer;
    public ContainerView noGroupPanelPropsContainer;

    public static DefaultFormView create(FormEntity entity) {
        DefaultFormView form = new DefaultFormView(entity);
        form.initProperties();
        return form;
    }

    protected DefaultFormView(FormEntity entity) {
        super(entity);

        caption = entity.title;

        FormContainerSet formSet = FormContainerSet.fillContainers(this, containerFactory);
        setComponentSID(formSet.getFormButtonContainer(), formSet.getFormButtonContainer().getSID());
        setComponentSID(formSet.getNoGroupPanelContainer(), formSet.getNoGroupPanelContainer().getSID());
        setComponentSID(formSet.getNoGroupPanelPropsContainer(), formSet.getNoGroupPanelPropsContainer().getSID());

        formButtonContainer = formSet.getFormButtonContainer();
        noGroupPanelContainer = formSet.getNoGroupPanelContainer();
        noGroupPanelPropsContainer = formSet.getNoGroupPanelPropsContainer();

        panelContainers.put(null, noGroupPanelContainer);
        panelPropsContainers.put(null, noGroupPanelPropsContainer);

        for (GroupObjectView groupObject : groupObjects) {
            if (groupObject.entity.treeGroup == null)
                addGroupObjectView(groupObject);
        }

        for (TreeGroupView treeGroup : treeGroups) {
            addTreeGroupView(treeGroup);
        }

        for (RegularFilterGroupView filterGroup : regularFilters) {
            addRegularFilterGroupView(filterGroup);
        }

        mainContainer.add(noGroupPanelContainer);
        mainContainer.add(formButtonContainer);
    }

    private void initProperties() {
        for (PropertyDrawView propertyDraw : properties) {
            addPropertyDrawView(propertyDraw);
        }

        initFormButtons();
    }

    protected Map<GroupObjectView, ContainerView> groupContainers = new HashMap<GroupObjectView, ContainerView>();

    public ContainerView getGroupObjectContainer(GroupObjectView groupObject) {
        return groupContainers.get(groupObject);
    }

    protected Map<GroupObjectView, ContainerView> gridContainers = new HashMap<GroupObjectView, ContainerView>();

    public ContainerView getGridContainer(GroupObjectView treeGroup) {
        return gridContainers.get(treeGroup);
    }

    protected Map<GroupObjectView, ContainerView> panelContainers = new HashMap<GroupObjectView, ContainerView>();

    public ContainerView getPanelContainer(GroupObjectView groupObject) {
        return panelContainers.get(groupObject);
    }

    protected transient Map<TreeGroupView, ContainerView> treeContainers = new HashMap<TreeGroupView, ContainerView>();

    public ContainerView getTreeContainer(TreeGroupView treeGroup) {
        return treeContainers.get(treeGroup);
    }

    protected final Map<GroupView, ContainerView> panelPropsContainers = new HashMap<GroupView, ContainerView>();

    public ContainerView getPanelPropsContainer(GroupObjectView groupObject) {
        return panelPropsContainers.get(groupObject);
    }

    protected transient Map<GroupView, ContainerView> controlsContainers = new HashMap<GroupView, ContainerView>();

    public ContainerView getControlsContainer(GroupObjectView groupObject) {
        return controlsContainers.get(groupObject);
    }

    public ContainerView getControlsContainer(TreeGroupView treeGroup) {
        return controlsContainers.get(treeGroup);
    }

    protected final Map<GroupView, ContainerView> toolbarPropsContainers = new HashMap<GroupView, ContainerView>();

    public ContainerView getToolbarPropsContainer(GroupObjectView groupObject) {
        return toolbarPropsContainers.get(groupObject);
    }

    public ContainerView getToolbarPropsContainer(TreeGroupView treeGroup) {
        return toolbarPropsContainers.get(treeGroup);
    }

    protected transient Map<GroupView, ContainerView> rightControlsContainers = new HashMap<GroupView, ContainerView>();

    public ContainerView getRightControlsContainer(GroupObjectView groupObject) {
        return rightControlsContainers.get(groupObject);
    }

    public ContainerView getRightControlsContainer(TreeGroupView treeGroup) {
        return rightControlsContainers.get(treeGroup);
    }

    protected final Map<GroupView, ContainerView> filtersContainers = new HashMap<GroupView, ContainerView>();

    public ContainerView getFilterContainer(GroupObjectView groupObject) {
        return filtersContainers.get(groupObject);
    }

    public ContainerView getFilterContainer(TreeGroupView treeGroup) {
        return filtersContainers.get(treeGroup);
    }

    protected transient Table<Optional<GroupObjectView>, AbstractGroup, ContainerView> groupPropertyContainers = HashBasedTable.create();

    public ContainerView getGroupPropertyContainer(GroupObjectView groupObject, AbstractGroup group) {
        return groupPropertyContainers.get(Optional.fromNullable(groupObject), group);
    }

    public ContainerView getGroupPropertyContainer(GroupObjectEntity groupObject, AbstractGroup group) {
        return getGroupPropertyContainer(get(groupObject), group);
    }


    public void addGroupObjectView(GroupObjectView goView) {
        GroupObjectContainerSet set = GroupObjectContainerSet.create(goView, containerFactory);

        mainContainer.add(set.getGroupContainer());
        groupContainers.put(goView, set.getGroupContainer());
        setComponentSID(set.getGroupContainer(), set.getGroupContainer().getSID());
        gridContainers.put(goView, set.getGridContainer());
        setComponentSID(set.getGridContainer(), set.getGridContainer().getSID());
        panelContainers.put(goView, set.getPanelContainer());
        setComponentSID(set.getPanelContainer(), set.getPanelContainer().getSID());
        panelPropsContainers.put(goView, set.getPanelPropsContainer());
        setComponentSID(set.getPanelPropsContainer(), set.getPanelPropsContainer().getSID());
        controlsContainers.put(goView, set.getControlsContainer());
        setComponentSID(set.getControlsContainer(), set.getControlsContainer().getSID());
        rightControlsContainers.put(goView, set.getRightControlsContainer());
        setComponentSID(set.getRightControlsContainer(), set.getRightControlsContainer().getSID());
        filtersContainers.put(goView, set.getFiltersContainer());
        setComponentSID(set.getFiltersContainer(), set.getFiltersContainer().getSID());
        toolbarPropsContainers.put(goView, set.getToolbarPropsContainer());
        setComponentSID(set.getToolbarPropsContainer(), set.getToolbarPropsContainer().getSID());

        //todo classChoosers

        if (goView.entity.isFixedPanel()) {
            set.getGroupContainer().flex = 0;
        }
    }

    public void addTreeGroupView(TreeGroupView treeGroup) {
        TreeGroupContainerSet treeSet = TreeGroupContainerSet.create(treeGroup, containerFactory);

        treeContainers.put(treeGroup, treeSet.getTreeContainer());
        setComponentSID(treeSet.getTreeContainer(), treeSet.getTreeContainer().getSID());
        controlsContainers.put(treeGroup, treeSet.getControlsContainer());
        setComponentSID(treeSet.getControlsContainer(), treeSet.getControlsContainer().getSID());
        rightControlsContainers.put(treeGroup, treeSet.getRightControlsContainer());
        setComponentSID(treeSet.getRightControlsContainer(), treeSet.getRightControlsContainer().getSID());
        filtersContainers.put(treeGroup, treeSet.getFiltersContainer());
        setComponentSID(treeSet.getFiltersContainer(), treeSet.getFiltersContainer().getSID());
        toolbarPropsContainers.put(treeGroup, treeSet.getToolbarPropsContainer());
        setComponentSID(treeSet.getToolbarPropsContainer(), treeSet.getToolbarPropsContainer().getSID());

        //вставляем перед первым groupObject в данной treeGroup
        mainContainer.addBefore(treeSet.getTreeContainer(), groupContainers.get(mgroupObjects.get(treeGroup.entity.groups.get(0))));
    }

    public void addPropertyDrawView(PropertyDrawView propertyDraw) {
        GroupObjectEntity groupObject = propertyDraw.entity.getToDraw(entity);
        GroupObjectView groupObjectView = mgroupObjects.get(groupObject);

        if (groupObjectView != null && propertyDraw.entity.toolbar) {
            ContainerView propertyContainer;
            if (groupObject.treeGroup != null) {
                propertyContainer = getToolbarPropsContainer(mtreeGroups.get(groupObject.treeGroup));
            } else {
                propertyContainer = getToolbarPropsContainer(mgroupObjects.get(groupObject));
            }

            propertyDraw.alignment = FlexAlignment.CENTER;

            propertyContainer.add(propertyDraw);
        } else {
            if (groupObject != null && groupObject.treeGroup != null) {
                TreeGroupView treeGroupView = mtreeGroups.get(groupObject.treeGroup);
                if (treeGroupView != null) {
                    treeGroupView.addPropertyDraw(groupObjectView, propertyDraw, properties);
                }
            } else if (!putInGrid(propertyDraw, groupObjectView)) {
                addPropertyDrawToLayout(groupObjectView, propertyDraw);
            } else {
                groupObjectView.addGridPropertyDraw(propertyDraw);
            }
        }
    }

    private void addPropertyDrawToLayout(GroupObjectView groupObject, PropertyDrawView propertyDraw) {
        if (groupObject != null && groupObject.entity.initClassView == ClassViewType.HIDE || propertyDraw.hide || propertyDraw.isForceHide()) {
            return;
        }

        // иерархическая структура контейнеров групп: каждый контейнер группы - это CONTAINERH,
        // в который сначала добавляется COLUMNS для свойств этой группы, а затем - контейнеры подгрупп
        AbstractGroup propertyParentGroup = propertyDraw.entity.parent;

        ContainerView propGroupContainer = getPropGroupContainer(groupObject, propertyParentGroup);
        propGroupContainer.add(propertyDraw);
    }

    private ContainerView getPropGroupContainer(GroupObjectView groupObject, AbstractGroup currentGroup) {
        if (currentGroup == null) {
            return panelPropsContainers.get(groupObject);
        }

        if (!currentGroup.createContainer) {
            return getPropGroupContainer(groupObject, currentGroup.parent);
        }

        //ищем в созданных
        ContainerView currentGroupContainer = groupPropertyContainers.get(Optional.fromNullable(groupObject), currentGroup);
        if (currentGroupContainer == null) {
            String currentGroupContainerSID = getPropertyGroupContainerSID(groupObject, currentGroup);

            //ищем по имени
            currentGroupContainer = getContainerBySID(currentGroupContainerSID);
            if (currentGroupContainer == null) {
                //сначала создаём контейнеры для верхних групп, чтобы соблюдался порядок
                getPropGroupContainer(groupObject, currentGroup.parent);

                //затем создаём контейнер для текущей группы
                currentGroupContainer = createContainer(currentGroup.caption, currentGroupContainerSID);
                currentGroupContainer.setType(ContainerType.COLUMNS);
                currentGroupContainer.columns = 4;

                panelContainers.get(groupObject).add(currentGroupContainer);
            }

            groupPropertyContainers.put(Optional.fromNullable(groupObject), currentGroup, currentGroupContainer);
        }

        return currentGroupContainer;
    }

    private static String getPropertyGroupContainerSID(GroupObjectView group, AbstractGroup propertyGroup) {
        String propertyGroupSID = propertyGroup.sID;
        if (propertyGroupSID.contains("_")) {
            String[] sids = propertyGroupSID.split("_", 2);
            propertyGroupSID = sids[1];
        }
        return (group == null ? "NOGROUP" : group.entity.sID) + "." + propertyGroupSID;
    }

    private void addRegularFilterGroupView(RegularFilterGroupView filterGroup) {
        ContainerView filterContainer;
        GroupObjectEntity groupObject = filterGroup.entity.getToDraw(entity);
        if (groupObject != null && groupObject.treeGroup != null) {
            filterContainer = getFilterContainer(mtreeGroups.get(groupObject.treeGroup));
        } else {
            filterContainer = getFilterContainer(mgroupObjects.get(groupObject));
        }
        if (filterContainer != null) {
            filterContainer.add(filterGroup);
        }
    }

    private void initFormButtons() {
        PropertyDrawView printFunction = get(entity.printActionPropertyDraw);
        setupFormButton(printFunction, KeyStrokes.getPrintKeyStroke(), "print.png", false);

        PropertyDrawView xlsFunction = get(entity.xlsActionPropertyDraw);
        setupFormButton(xlsFunction, KeyStrokes.getXlsKeyStroke(), "xls.png", false);

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

        ContainerView leftControlsContainer = createContainer(null, "leftControls");
        leftControlsContainer.setType(ContainerType.CONTAINERH);
        leftControlsContainer.childrenAlignment = Alignment.LEADING;
        leftControlsContainer.flex = 0;

        ContainerView rightControlsContainer = createContainer(null, "rightControls");
        rightControlsContainer.setType(ContainerType.CONTAINERH);
        rightControlsContainer.childrenAlignment = Alignment.TRAILING;
        rightControlsContainer.flex = 1;

        leftControlsContainer.add(printFunction);
        leftControlsContainer.add(xlsFunction);
        leftControlsContainer.add(editFunction);
        leftControlsContainer.add(dropFunction);

        rightControlsContainer.add(refreshFunction);
        rightControlsContainer.add(applyFunction);
        rightControlsContainer.add(cancelFunction);
        rightControlsContainer.add(okFunction);
        rightControlsContainer.add(closeFunction);

        formButtonContainer.add(leftControlsContainer);
        formButtonContainer.add(rightControlsContainer);

        if (!entity.isModal()) {
            removeComponent(okFunction, false);
            removeComponent(closeFunction, false);
        }
        if (!entity.isDialog()) {
            removeComponent(dropFunction, false);
        }
    }

    private void setupFormButton(PropertyDrawView action, KeyStroke editKey, String iconPath, boolean showCaption) {
        action.editKey = editKey;
        action.focusable = false;
        action.alignment = FlexAlignment.STRETCH;
        action.iconPath = iconPath;
        action.showCaption = showCaption;

        if (iconPath != null) {
            action.showEditKey = false;
        }
    }
}
