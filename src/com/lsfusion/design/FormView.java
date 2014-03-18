package com.lsfusion.design;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.design.model.*;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFPropReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormView {
    public ContainerView mainContainer;

    private final Map<String, ComponentView> sidToComponent = new HashMap<String, ComponentView>();

    protected ContainerFactory containerFactory;

    public List<TreeGroupView> treeGroups = new ArrayList<TreeGroupView>();
    public List<GroupObjectView> groupObjects = new ArrayList<GroupObjectView>();
    public List<PropertyDrawView> properties = new ArrayList<PropertyDrawView>();

    public FormView() {
        mainContainer = new ContainerView("main");
        setComponentSID(mainContainer, getMainContainerSID());

        containerFactory = new ContainerFactory();
    }

    public void extendForm(LSFFormExtend formExtend) {
        for (LSFGroupObjectDeclaration gobjDecl : formExtend.getGroupObjectDecls()) {
            LSFFormGroupObjectDeclaration parent = null;
            if (gobjDecl.getParent() instanceof LSFFormGroupObjectDeclaration) {
                parent = (LSFFormGroupObjectDeclaration) gobjDecl.getParent();
            }
            addGroupObjectBase(new GroupObjectView(gobjDecl.getDeclName(), parent != null ? parent.getFormGroupObjectViewType() : null, parent != null ? parent.getFormGroupObjectPageSize() : null));
        }

        for (LSFFormTreeGroupObjectDeclaration tgobjDecl : formExtend.getTreeGroupDecls()) {
            addTreeGroupBase(new TreeGroupView(tgobjDecl.getFormCommonGroupObject().getDeclName()));
        }

        for (Map.Entry<LSFPropertyDrawDeclaration, Pair<LSFFormPropertyOptionsList, LSFFormPropertyOptionsList>> entry : formExtend.getPropertyDrawDeclsWithOptions().entrySet()) {
            LSFPropReference propUsage = PsiTreeUtil.findChildOfType(entry.getKey(), LSFPropReference.class);
            LSFPropertyOptions propertyOptions = null;
            if (propUsage != null) {
                LSFPropDeclaration propDeclaration = propUsage.resolveDecl();
                if (propDeclaration != null) {
                    propertyOptions = ((LSFPropertyStatement) propDeclaration).getPropertyOptions();
                }
            }
            addPropertyDrawBase(new PropertyDrawView(entry.getKey().getDeclName(), propertyOptions, entry.getValue()));
        }
    }

    public ContainerView getMainContainer() {
        return mainContainer;
    }

    public GroupObjectView addGroupObject(LSFGroupObjectDeclaration groupObject) {
        return addGroupObjectBase(new GroupObjectView(groupObject.getDeclName()));
    }

    private GroupObjectView addGroupObjectBase(GroupObjectView groupObjectView) {
        groupObjects.add(groupObjectView);
        setComponentSID(groupObjectView.grid, getGridSID(groupObjectView));
        setComponentSID(groupObjectView.showType, getShowTypeSID(groupObjectView));
        setComponentSID(groupObjectView.toolbar, getToolbarSID(groupObjectView));
        setComponentSID(groupObjectView.filter, getFilterSID(groupObjectView));

        for (ObjectView object : groupObjectView) {
            setComponentSID(object.classChooser, getClassChooserSID(object));
        }
        addGroupObjectView(groupObjectView);
        return groupObjectView;
    }

    public void addGroupObjectView(GroupObjectView goView) {
    }

    public TreeGroupView addTreeGroup(LSFFormTreeGroupObjectDeclaration treeGroup) {
        return addTreeGroupBase(new TreeGroupView(treeGroup.getFormCommonGroupObject().getDeclName()));
    }

    private TreeGroupView addTreeGroupBase(TreeGroupView treeGroupView) {
        treeGroups.add(treeGroupView);
        addTreeGroupView(treeGroupView);
        return treeGroupView;
    }

    public void addTreeGroupView(TreeGroupView treeGroup) {
    }

    public PropertyDrawView addPropertyDraw(LSFPropertyDrawDeclaration property) {
        PropertyDrawView propertyDrawView = addPropertyDrawBase(new PropertyDrawView(property.getDeclName()));
        addPropertyDrawView(propertyDrawView);
        return propertyDrawView;
    }

    private PropertyDrawView addPropertyDrawBase(PropertyDrawView propertyDrawView) {
        properties.add(propertyDrawView);
        sidToComponent.put(propertyDrawView.sID, propertyDrawView);
        return propertyDrawView;
    }

    public void addPropertyDrawView(PropertyDrawView pdView) {
    }

    public ContainerView createContainer() {
        return createContainer(null);
    }

    public ContainerView createContainer(String caption) {
        return createContainer(caption, null);
    }

    public ContainerView createContainer(String caption, String sID) {
        ContainerView container = new ContainerView();
//        container.setCaption(caption);
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

    public static String getMainContainerSID() {
        return "main";
    }

    private static String getTreeSID(TreeGroupView component) {
        return getTreeSID(component.sID);
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
        return getGridSID(component.sID);
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
        return getShowTypeSID(component.sID);
    }

    public static String getShowTypeSID(String sID) {
        return sID + ".showType";
    }

    private static String getClassChooserSID(ObjectView component) {
        return getClassChooserSID(component.sID);
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
