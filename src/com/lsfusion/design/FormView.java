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

    protected PropertyDrawView printButton;
    protected PropertyDrawView editButton;
    protected PropertyDrawView xlsButton;
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

        mainContainer = new ContainerView("main");
        setComponentSID(mainContainer, getMainContainerSID());

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
                addRegularFilterGroupBase(filterGroup);
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
        return !caption.isEmpty() ? caption : entity.sID;
    }

    public ContainerView getMainContainer() {
        return mainContainer;
    }

    private GroupObjectView addGroupObjectBase(GroupObjectEntity groupObjectEntity) {
        GroupObjectView groupObjectView = new GroupObjectView(groupObjectEntity);
        groupObjects.add(groupObjectView);

        setComponentSID(groupObjectView.grid, getGridSID(groupObjectView));
        setComponentSID(groupObjectView.showType, getShowTypeSID(groupObjectView));
        setComponentSID(groupObjectView.toolbar, getToolbarSID(groupObjectView));
        setComponentSID(groupObjectView.filter, getFilterSID(groupObjectView));

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

        setComponentSID(treeGroupView, getTreeSID(treeGroupView));
        setComponentSID(treeGroupView.toolbar, getToolbarSID(treeGroupView));
        setComponentSID(treeGroupView.filter, getFilterSID(treeGroupView));
        mtreeGroups.put(treeGroupEntity, treeGroupView);
    }

    private void addRegularFilterGroupView(RegularFilterGroupView filterGroupView) {
        mfilters.put(filterGroupView.entity, filterGroupView);
    }

    private RegularFilterGroupView addRegularFilterGroupBase(RegularFilterGroupEntity filterGroup) {
        RegularFilterGroupView filterGroupView = new RegularFilterGroupView(this, filterGroup);
        regularFilters.add(filterGroupView);
        addRegularFilterGroupView(filterGroupView);

        mfilters.put(filterGroupView.entity, filterGroupView);
        setComponentSID(filterGroupView, getRegularFilterGroupSID(filterGroupView));
        return filterGroupView;
    }

    private PropertyDrawView addPropertyDrawBase(PropertyDrawEntity propertyDrawEntity/*, Pair<Boolean, PropertyDrawView> relative*/) {
        PropertyDrawView propertyDrawView = new PropertyDrawView(propertyDrawEntity);
        properties.add(propertyDrawView);

        sidToComponent.put(propertyDrawView.getSID(), propertyDrawView);
        mproperties.put(propertyDrawEntity, propertyDrawView);

        return propertyDrawView;
    }

    private void addPropertyDrawView(PropertyDrawView propertyDraw) {
        GroupObjectEntity groupObject = propertyDraw.entity.getToDraw(entity);
        GroupObjectView groupObjectView = mgroupObjects.get(groupObject);

        if (groupObject != null && groupObject.treeGroup != null) {
            TreeGroupView treeGroupView = mtreeGroups.get(groupObject.treeGroup);
            if (treeGroupView != null) {
                treeGroupView.addPropertyDraw(groupObjectView, propertyDraw, properties);
            }
        } else if (putInGrid(propertyDraw, groupObjectView)) {
            groupObjectView.addGridPropertyDraw(propertyDraw);
        }
    }

    public boolean putInGrid(PropertyDrawView property, GroupObjectView groupObjectView) {
        if (groupObjectView != null) {
            if (!groupObjectView.entity.isFixedPanel() && groupObjectView.entity.initClassView == ClassViewType.GRID && !property.isForcedPanel()) {
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
        printButton = setupFormButton(entity.printActionPropertyDraw, "print");
        editButton = setupFormButton(entity.editActionPropertyDraw, "edit");
        xlsButton = setupFormButton(entity.xlsActionPropertyDraw, "xls");
        refreshButton = setupFormButton(entity.refreshActionPropertyDraw, "refresh");
        applyButton = setupFormButton(entity.applyActionPropertyDraw, "apply");
        cancelButton = setupFormButton(entity.cancelActionPropertyDraw, "cancel");
        okButton = setupFormButton(entity.okActionPropertyDraw, "ok");
        closeButton = setupFormButton(entity.closeActionPropertyDraw, "close");
        dropButton = setupFormButton(entity.dropActionPropertyDraw, "drop");
    }

    private PropertyDrawView setupFormButton(PropertyDrawEntity function, String type) {
        PropertyDrawView functionView = getProperty(function);
        setComponentSID(functionView, getClientFunctionSID(type));
        return functionView;
    }

    public ContainerView createContainer(String caption, String sID) {
        ContainerView container = new ContainerView();
        container.setCaption(caption);
        container.setSID(sID);
        if (sID != null) {
            sidToComponent.put(sID, container);
        }
        return container;
    }

    protected void setComponentSID(ComponentView component, String sid) {
        component.setSID(sid);
        sidToComponent.put(component.getSID(), component);
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

    public static String getMainContainerSID() {
        return "main";
    }

    private static String getTreeSID(TreeGroupView component) {
        return getTreeSID(component.getSID());
    }

    public static String getTreeSID(String sID) {
        return sID + ".tree";
    }

    private static String getRegularFilterGroupSID(RegularFilterGroupView component) {
        return getRegularFilterGroupSID(component.getSID());
    }

    public static String getRegularFilterGroupSID(String sID) {
        return "filters." + sID;
    }

    private static String getGridSID(GroupObjectView component) {
        return getGridSID(component.getSID());
    }

    public static String getGridSID(String sID) {
        return sID + ".grid";
    }

    private static String getToolbarSID(GroupView component) {
        return getToolbarSID(component.getSID());
    }

    public static String getToolbarSID(String sID) {
        return sID + ".toolbar";
    }

    private static String getFilterSID(GroupView component) {
        return getFilterSID(component.getSID());
    }

    public static String getFilterSID(String sID) {
        return sID + ".filter";
    }

    private static String getShowTypeSID(GroupObjectView component) {
        return getShowTypeSID(component.getSID());
    }

    public static String getShowTypeSID(String sID) {
        return sID + ".showType";
    }

    private static String getClassChooserSID(ObjectView component) {
        return getClassChooserSID(component.getSID());
    }

    public static String getClassChooserSID(String sID) {
        return sID + ".classChooser";
    }

    public static String getClientFunctionSID(String type) {
        return "functions." + type;
    }

    public class ContainerFactory {
        public ContainerView createContainer() {
            return new ContainerView();
        }
    }
}
