package com.lsfusion.design;

import com.lsfusion.design.model.*;
import com.lsfusion.design.model.entity.*;
import com.lsfusion.design.ui.ClassViewType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormView {
    public String caption = "";

    public FormEntity entity;

    public ContainerView mainContainer;

    public Integer overridePageWidth;

    protected PropertyDrawView editButton;
    protected PropertyDrawView dropButton;
    protected PropertyDrawView refreshButton;
    protected PropertyDrawView applyButton;
    protected PropertyDrawView cancelButton;
    protected PropertyDrawView okButton;
    protected PropertyDrawView closeButton;

    private final Map<String, ComponentView> sidToComponent = new HashMap<>();

    protected ContainerFactory containerFactory;

    public List<TreeGroupView> treeGroups = new ArrayList<>();
    public List<GroupObjectView> groupObjects = new ArrayList<>();
    public List<PropertyDrawView> properties = new ArrayList<>();
    public List<RegularFilterGroupView> regularFilters = new ArrayList<>();

    protected Map<TreeGroupEntity, TreeGroupView> mtreeGroups = new HashMap<>();

    public TreeGroupView get(TreeGroupEntity treeGroup) {
        return mtreeGroups.get(treeGroup);
    }

    protected Map<GroupObjectEntity, GroupObjectView> mgroupObjects = new HashMap<>();

    public GroupObjectView get(GroupObjectEntity groupObject) {
        return mgroupObjects.get(groupObject);
    }

    protected Map<ObjectEntity, ObjectView> mobjects = new HashMap<>();

    public ObjectView get(ObjectEntity object) {
        return mobjects.get(object);
    }

    protected Map<PropertyDrawEntity, PropertyDrawView> mproperties = new HashMap<>();

    public PropertyDrawView get(PropertyDrawEntity property) {
        return mproperties.get(property);
    }

    protected Map<RegularFilterGroupEntity, RegularFilterGroupView> mfilters = new HashMap<>();

    public RegularFilterGroupView get(RegularFilterGroupEntity filterGroup) {
        return mfilters.get(filterGroup);
    }

    public static FormView create(FormEntity entity) {
        FormView form = new FormView(entity);
        form.initProperties();
        return form;
    }

    protected FormView(FormEntity entity) {
        this.entity = entity;

        String mainContainerSID = getBoxContainerSID();
        mainContainer = new ContainerView(mainContainerSID);
        addComponentToMapping(mainContainer);

        containerFactory = new ContainerFactory();

        for (TreeGroupEntity treeGroup : entity.treeGroups) {
            addTreeGroupBase(treeGroup);
        }

        for (GroupObjectEntity group : entity.groups) {
            addGroupObjectBase(group);
        }

        for (PropertyDrawEntity property : entity.propertyDraws) {
            addPropertyDrawBase(property);
        }

        for (RegularFilterGroupEntity filterGroup : entity.regularFilterGroups) {
            if (!filterGroup.filters.isEmpty()) {
                addFilterGroupBase(filterGroup);
            }
        }
    }

    private void initProperties() {
        initButtons();

        for (PropertyDrawView property : properties) {
            addPropertyDrawView(property);
        }
    }

    public String getCaption() {
        return caption != null && !caption.isEmpty() ? caption : entity.sID;
    }

    public ContainerView getMainContainer() {
        return mainContainer;
    }

    private GroupObjectView addGroupObjectBase(GroupObjectEntity groupObjectEntity) {
        GroupObjectView groupObjectView = new GroupObjectView(groupObjectEntity);
        groupObjects.add(groupObjectView);

        setComponentSID(groupObjectView.grid, getGridSID(groupObjectView));
        setComponentSID(groupObjectView.showType, getShowTypeSID(groupObjectView));
        setComponentSID(groupObjectView.toolbarSystem, getToolbarSystemSID(groupObjectView));
        setComponentSID(groupObjectView.userFilter, getUserFilterSID(groupObjectView));

        for (ObjectView object : groupObjectView) {
            setComponentSID(object.classChooser, getClassChooserSID(object));
            mobjects.put(object.entity, object);
        }

        mgroupObjects.put(groupObjectView.entity, groupObjectView);
        return groupObjectView;
    }

    private void addTreeGroupBase(TreeGroupEntity treeGroupEntity) {
        TreeGroupView treeGroupView = new TreeGroupView(treeGroupEntity);
        treeGroups.add(treeGroupView);

        setComponentSID(treeGroupView, getGridSID(treeGroupView));
        setComponentSID(treeGroupView.toolbarSystem, getToolbarSystemSID(treeGroupView));
        setComponentSID(treeGroupView.userFilter, getUserFilterSID(treeGroupView));
        mtreeGroups.put(treeGroupEntity, treeGroupView);
    }

    private void addRegularFilterGroupView(RegularFilterGroupView filterGroupView) {
        mfilters.put(filterGroupView.entity, filterGroupView);
    }

    private RegularFilterGroupView addFilterGroupBase(RegularFilterGroupEntity filterGroup) {
        RegularFilterGroupView filterGroupView = new RegularFilterGroupView(this, filterGroup);
        regularFilters.add(filterGroupView);
        addRegularFilterGroupView(filterGroupView);

        mfilters.put(filterGroupView.entity, filterGroupView);
        setComponentSID(filterGroupView, getFilterGroupSID(filterGroupView));
        return filterGroupView;
    }

    private PropertyDrawView addPropertyDrawBase(PropertyDrawEntity propertyDrawEntity/*, Pair<Boolean, PropertyDrawView> relative*/) {
        PropertyDrawView propertyDrawView = new PropertyDrawView(propertyDrawEntity);
        properties.add(propertyDrawView);

        addComponentToMapping(propertyDrawView);
        mproperties.put(propertyDrawEntity, propertyDrawView);

        return propertyDrawView;
    }

    private void addPropertyDrawView(PropertyDrawView propertyDraw) {
        addGridDrawView(propertyDraw);
    }
    
    protected boolean addGridDrawView(PropertyDrawView propertyDraw) { // в том числе и HIDE
        GroupObjectEntity groupObject = propertyDraw.entity.getToDraw(entity);
        GroupObjectView groupObjectView = mgroupObjects.get(groupObject);

        if (groupObjectView != null && groupObjectView.entity.initClassView == ClassViewType.HIDE || propertyDraw.hide || propertyDraw.isForceHide()) {
            return true;
        }
        if (putInGrid(propertyDraw, groupObjectView)) {
            if (groupObject != null && groupObject.isInTree()) {
                TreeGroupView treeGroupView = mtreeGroups.get(groupObject.treeGroup);
                if (treeGroupView != null) {
                    treeGroupView.addPropertyDraw(groupObjectView, propertyDraw, properties);
                }
            } else
                groupObjectView.addGridPropertyDraw(propertyDraw);
        } else
            return false;
        
        return true;        
    }

    public boolean putInGrid(PropertyDrawView property, GroupObjectView groupObjectView) {
        if (groupObjectView != null) {
            if (!groupObjectView.entity.isFixedPanel() && groupObjectView.entity.initClassView == ClassViewType.GRID && !property.isForcedPanel() && !property.entity.isToolbar(entity)) {
                return true;
            }
        }
        return false;
    }

    public PropertyDrawView getProperty(PropertyDrawEntity entity) {
        if (entity == null) {
            return null;
        }
        for (PropertyDrawView property : properties) {
            if (entity.equals(property.entity)) {
                return property;
            }
        }
        return null;
    }

    private void initButtons() {
        editButton = getProperty(entity.editActionPropertyDraw);
        refreshButton = getProperty(entity.refreshActionPropertyDraw);
        applyButton = getProperty(entity.applyActionPropertyDraw);
        cancelButton = getProperty(entity.cancelActionPropertyDraw);
        okButton = getProperty(entity.okActionPropertyDraw);
        closeButton = getProperty(entity.closeActionPropertyDraw);
        dropButton = getProperty(entity.dropActionPropertyDraw);
    }

    public ContainerView createContainer(String caption, String sID) {
        ContainerView container = new ContainerView();
        container.setCaption(caption);
        setComponentSID(container, sID);
        return container;
    }

    protected void setComponentSID(ComponentView component, String sid) {
        component.setSID(sid);
        addComponentToMapping(component);
    }
    
    public void addComponentToMapping(ComponentView component) {
        if (component.getSID() != null) {
            sidToComponent.put(component.getSID(), component);
        }
    } 

    public ComponentView getComponentBySID(String sid) {
        return sidToComponent.get(sid);
    }

    public ContainerView getContainerBySID(String sid) {
        ComponentView component = getComponentBySID(sid);
        if (component != null && !(component instanceof ContainerView)) {
            throw new IllegalStateException(sid + " component has to be container");
        }
        return (ContainerView) component;
    }

    public void removeComponent(ComponentView component) {
        component.removeFromParent();

        //не удаляем компоненты (не-контейнеры) из пула, чтобы можно было опять их использовать в настройке
        if (component instanceof ContainerView) {
            removeContainerFromMapping((ContainerView) component);
        }
    }

    public void removeContainerFromMapping(ContainerView container) {
        sidToComponent.remove(container.getSID());
    }

//  SID BLOCK    
    public static String getBoxContainerSID() {
        return FormContainerSet.BOX_CONTAINER;
    }

    public static String getTreeSID(String sID) {
        return TreeGroupContainerSet.TREE_PREFIX + " " + sID;
    }

    private static String getFilterGroupSID(RegularFilterGroupView component) {
        return getFilterGroupSID(component.getSID());
    }

    public static String getFilterGroupSID(String sID) {
        return GroupObjectContainerSet.FILTERGROUP_COMPONENT + "(" + sID + ")";
    }

    public static String getGridSID(PropertyGroupContainerView containerView) {
        return getGridSID(containerView.getPropertyGroupContainerSID());        
    }

    public static String getGridSID(String sID) {
        return GroupObjectContainerSet.GRID_COMPONENT + "(" + sID + ")";
    }

    public static String getToolbarSystemSID(PropertyGroupContainerView containerView) {
        return getToolbarSystemSID(containerView.getPropertyGroupContainerSID());
    }

    public static String getToolbarSystemSID(String sID) {
        return GroupObjectContainerSet.TOOLBAR_SYSTEM_COMPONENT + "(" + sID + ")";
    }

    public static String getUserFilterSID(PropertyGroupContainerView containerView) {
        return getUserFilterSID(containerView.getPropertyGroupContainerSID());
    }

    public static String getUserFilterSID(String sID) {
        return GroupObjectContainerSet.USERFILTER_COMPONENT + "(" + sID + ")";
    }

    public static String getShowTypeSID(PropertyGroupContainerView containerView) {
        return getShowTypeSID(containerView.getPropertyGroupContainerSID());
    }
    
    public static String getShowTypeSID(String sID) {
        return GroupObjectContainerSet.SHOWTYPE_COMPONENT + "(" + sID + ")";
    }

    private static String getClassChooserSID(ObjectView component) {
        return getClassChooserSID(component.getSID());
    }

    public static String getClassChooserSID(String sID) {
        return GroupObjectContainerSet.CLASSCHOOSER_COMPONENT + "(" + sID + ")";
    }

    public static String getPropertyDrawSID(String name) {
        return "PROPERTY(" + name + ")";
    }

    public static String getPropertyDrawSID(String alias, String name, List<String> objectNames) {
        return getPropertyDrawSID(getPropertyDrawName(alias, name, objectNames));
    }
    
    public static String getPropertyDrawName(String alias, String name, List<String> objectNames) {
        StringBuilder propName;
        if (alias != null) {
            propName = new StringBuilder(alias);
        } else {
            propName = new StringBuilder(name + "(");
            for (String objectUsage : objectNames) {
                propName.append(objectUsage);
                if (objectNames.indexOf(objectUsage) < objectNames.size() - 1) {
                    propName.append(",");
                }
            }
            propName.append(")");
        }
        return propName.toString();
    }

    public class ContainerFactory {
        public ContainerView createContainer() {
            return new ContainerView();
        }
    }
    
    
}
