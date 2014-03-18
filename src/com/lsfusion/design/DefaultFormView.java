package com.lsfusion.design;

import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.GroupObjectView;
import com.lsfusion.design.model.PropertyDrawView;
import com.lsfusion.design.model.TreeGroupView;

import java.util.HashMap;
import java.util.Map;

public class DefaultFormView extends FormView {


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

    //    protected final Map<ServerIdentitySerializable, ContainerView> panelPropsContainers = new HashMap<ServerIdentitySerializable, ContainerView>();
//    public ContainerView getPanelPropsContainer(GroupObjectView groupObject) { return panelPropsContainers.get(groupObject); }
//
//    protected transient Map<ServerIdentitySerializable, ContainerView> controlsContainers = new HashMap<ServerIdentitySerializable, ContainerView>();
//    public ContainerView getControlsContainer(GroupObjectView groupObject) { return controlsContainers.get(groupObject); }
//    public ContainerView getControlsContainer(TreeGroupView treeGroup) { return controlsContainers.get(treeGroup); }
//
//    protected final Map<ServerIdentitySerializable, ContainerView> toolbarPropsContainers = new HashMap<ServerIdentitySerializable, ContainerView>();
//    public ContainerView getToolbarPropsContainer(GroupObjectView groupObject) { return toolbarPropsContainers.get(groupObject); }
//    public ContainerView getToolbarPropsContainer(TreeGroupView treeGroup) { return toolbarPropsContainers.get(treeGroup); }
//
//    protected transient Map<ServerIdentitySerializable, ContainerView> rightControlsContainers = new HashMap<ServerIdentitySerializable, ContainerView>();
//    public ContainerView getRightControlsContainer(GroupObjectView groupObject) { return rightControlsContainers.get(groupObject); }
//    public ContainerView getRightControlsContainer(TreeGroupView treeGroup) { return rightControlsContainers.get(treeGroup); }
//
//    protected final Map<ServerIdentitySerializable,ContainerView> filtersContainers = new HashMap<ServerIdentitySerializable, ContainerView>();
//    public ContainerView getFilterContainer(GroupObjectView groupObject) { return filtersContainers.get(groupObject); }
//    public ContainerView getFilterContainer(TreeGroupView treeGroup) { return filtersContainers.get(treeGroup); }
//
//    protected transient Table<Optional<GroupObjectView>, AbstractGroup, ContainerView> groupPropertyContainers = HashBasedTable.create();
//    public ContainerView getGroupPropertyContainer(GroupObjectView groupObject, AbstractGroup group) { return groupPropertyContainers.get(Optional.fromNullable(groupObject), group); }
//    public ContainerView getGroupPropertyContainer(GroupObjectEntity groupObject, AbstractGroup group) { return getGroupPropertyContainer(get(groupObject), group); }

    public DefaultFormView() {
//        super();
//        
//        for (GroupObjectView groupObject : groupObjects) {
//            addGroupObjectView(groupObject);
//        }
//
//        for (TreeGroupView treeGroup : treeGroups) {
//            addTreeGroupView(treeGroup);
//        }
//
//        for (PropertyDrawView propertyDraw : properties) {
//            addPropertyDrawView(propertyDraw);
//        }
    }

    public void addGroupObjectView(GroupObjectView goView) {
        GroupObjectContainerSet set = GroupObjectContainerSet.create(goView, containerFactory);

        mainContainer.add(set.getGroupContainer());
        groupContainers.put(goView, set.getGroupContainer());
        setComponentSID(set.getGroupContainer(), set.getGroupContainer().getSID());
        gridContainers.put(goView, set.getGridContainer());
        setComponentSID(set.getGridContainer(), set.getGridContainer().getSID());
        panelContainers.put(goView, set.getPanelContainer());
        setComponentSID(set.getPanelPropsContainer(), set.getPanelPropsContainer().getSID());
    }

    public void addTreeGroupView(TreeGroupView treeGroup) {
        TreeGroupContainerSet treeSet = TreeGroupContainerSet.create(treeGroup, containerFactory);

        treeContainers.put(treeGroup, treeSet.getTreeContainer());
        setComponentSID(treeSet.getTreeContainer(), treeSet.getTreeContainer().getSID());
//        controlsContainers.put(treeGroup, treeSet.getControlsContainer());
//        setComponentSID(treeSet.getControlsContainer(), treeSet.getControlsContainer().getSID());
//        rightControlsContainers.put(treeGroup, treeSet.getRightControlsContainer());
//        setComponentSID(treeSet.getRightControlsContainer(), treeSet.getRightControlsContainer().getSID());
//        filtersContainers.put(treeGroup, treeSet.getFiltersContainer());
//        setComponentSID(treeSet.getFiltersContainer(), treeSet.getFiltersContainer().getSID());
//        toolbarPropsContainers.put(treeGroup, treeSet.getToolbarPropsContainer());
//        setComponentSID(treeSet.getToolbarPropsContainer(), treeSet.getToolbarPropsContainer().getSID());

        //вставляем перед первым groupObject в данной treeGroup
//        mainContainer.addBefore(treeSet.getTreeContainer(), groupContainers.get(mgroupObjects.get(treeGroup.entity.getGroups().get(0))));
        mainContainer.addFirst(treeSet.getTreeContainer());
    }

//    @Override
//    public PropertyDrawView addPropertyDraw(LSFPropertyDrawDeclaration propDecl) {
//        PropertyDrawView propertyDrawView = super.addPropertyDraw(propDecl);
//        addPropertyDrawView(propertyDrawView);
//        return propertyDrawView;
//    }

    public void addPropertyDrawView(PropertyDrawView pdView) {

    }


}
