package com.lsfusion.design.model.entity;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.ModalityType;
import com.lsfusion.design.model.property.*;
import com.lsfusion.lang.classes.ObjectClass;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFPropReference;
import com.lsfusion.lang.psi.stubs.types.indexes.PropIndex;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FormEntity {
    private LSFFile file;

    public List<GroupObjectEntity> groups = new ArrayList<GroupObjectEntity>();
    public List<TreeGroupEntity> treeGroups = new ArrayList<TreeGroupEntity>();
    public List<PropertyDrawEntity> propertyDraws = new ArrayList<PropertyDrawEntity>();
    public List<RegularFilterGroupEntity> regularFilterGroups = new ArrayList<RegularFilterGroupEntity>();

    public PropertyDrawEntity printActionPropertyDraw;
    public PropertyDrawEntity editActionPropertyDraw;
    public PropertyDrawEntity xlsActionPropertyDraw;
    public PropertyDrawEntity dropActionPropertyDraw;
    public PropertyDrawEntity refreshActionPropertyDraw;
    public PropertyDrawEntity applyActionPropertyDraw;
    public PropertyDrawEntity cancelActionPropertyDraw;
    public PropertyDrawEntity okActionPropertyDraw;
    public PropertyDrawEntity closeActionPropertyDraw;

    public ModalityType modalityType = ModalityType.DOCKED;

    public String title;
    public String sID;

    public FormEntity(LSFFile file) {
        this.file = file;
        initFormButtons();
    }

    public Project getProject() {
        return file.getProject();
    }

    public void extendForm(LSFFormExtend formExtend) {
        LSFFormDecl formDecl = formExtend.getFormDecl();
        if (formDecl != null) {
            title = formDecl.getCaption();
            sID = formDecl.getDeclName();

            LSFModalityTypeLiteral modalityLiteral = formDecl.getModalityTypeLiteral();
            if (modalityLiteral != null) {
                modalityType = ModalityType.valueOf(modalityLiteral.getText());
            }
        }

        for (LSFFormGroupObjectDeclaration formGroupObjectDeclaration : formExtend.getFormGroupObjectDeclarations()) {
            addGroupObject(formGroupObjectDeclaration.getFormCommonGroupObject(), formGroupObjectDeclaration.getFormGroupObjectViewType(), formGroupObjectDeclaration.getFormGroupObjectPageSize());
        }

        for (LSFFormTreeGroupObjectList tgobjDecl : formExtend.getFormTreeGroupObjectListList()) {
            addTreeGroupObject(tgobjDecl);
        }

        extractPropertyDraws(formExtend);

        for (LSFFormFilterGroupDeclaration lsfFormFilterGroupDeclaration : formExtend.getFormFilterGroupDeclarationList()) {
            addRegularFilterGroup(lsfFormFilterGroupDeclaration);
        }

    }

    private GroupObjectEntity addGroupObject(LSFGroupObjectDeclaration gobjDecl, LSFFormGroupObjectViewType viewType, LSFFormGroupObjectPageSize pageSize) {
        GroupObjectEntity groupObject = new GroupObjectEntity(gobjDecl.getDeclName(), viewType, pageSize);
        Collection<LSFFormObjectDeclaration> objectDecarations = gobjDecl.findObjectDecarations();
        for (LSFFormObjectDeclaration objDecl : objectDecarations) {
            objDecl.getClassName();
            groupObject.add(new ObjectEntity(objDecl.getDeclName(), objDecl.resolveClass().getCommonClass()));
        }
        groups.add(groupObject);

        return groupObject;
    }

    private void addRegularFilterGroup(LSFFormFilterGroupDeclaration filterGroupDeclaration) {
        RegularFilterGroupEntity filterGroup = new RegularFilterGroupEntity(filterGroupDeclaration.getFilterGroupName().getSimpleName().getName());
        for (LSFRegularFilterDeclaration regularFilterDeclaration : filterGroupDeclaration.getRegularFilterDeclarationList()) {

            List<ObjectEntity> params = new ArrayList<ObjectEntity>();
            for (LSFExprParamDeclaration paramDeclaration : regularFilterDeclaration.getFormFilterDeclaration().getPropertyExpression().resolveParams()) {
                params.add(getObject(paramDeclaration.getDeclName()));
            }

            List<LSFStringLiteral> stringLiterals = regularFilterDeclaration.getStringLiteralList();
            boolean isDefault = regularFilterDeclaration.getFilterSetDefault() != null;
            RegularFilterEntity filter = new RegularFilterEntity(stringLiterals.get(0).getValue(), KeyStroke.getKeyStroke(stringLiterals.get(1).getValue()), params, isDefault);
            filterGroup.addFilter(filter);
        }
        regularFilterGroups.add(filterGroup);
    }

    private void extractPropertyDraws(LSFFormExtend formExtend) {
        for (LSFFormPropertiesList formProperties : formExtend.getFormPropertiesListList()) {
            LSFFormPropertyOptionsList commonOptions = formProperties.getFormPropertyOptionsList();
            if (commonOptions != null) {
                LSFFormMappedPropertiesList propertyList = formProperties.getFormMappedPropertiesList();
                if (propertyList != null) {
                    Pair<Boolean, PropertyDrawEntity> relativeProsition = getRelativePosition(commonOptions);
                    List<LSFFormPropertyDrawMappedDecl> mappedDeclList = propertyList.getFormPropertyDrawMappedDeclList();
                    boolean reverseFor = relativeProsition != null && !relativeProsition.first;
                    for (int i = reverseFor ? mappedDeclList.size() - 1 : 0; (reverseFor && i >= 0) || (!reverseFor && i < mappedDeclList.size()); i = i + (reverseFor ? -1 : 1)) {
                        LSFFormPropertyDrawMappedDecl prop = mappedDeclList.get(i);
                        LSFObjectUsageList objectUsageList = prop.getFormPropertyObject().getObjectUsageList();

                        String alias = prop.getSimpleName() != null ? prop.getSimpleName().getName() : null;
                        addPropertyWithOptions(alias, prop.getFormPropertyObject().getFormPropertyName(), commonOptions, prop.getFormPropertyOptionsList(), relativeProsition, objectUsageList);
                    }
                }
            } else {
                LSFFormMappedNamePropertiesList mappedProps = formProperties.getFormMappedNamePropertiesList();
                if (mappedProps != null) {
                    LSFObjectUsageList objectUsageList = mappedProps.getObjectUsageList();

                    commonOptions = mappedProps.getFormPropertyOptionsList();
                    Pair<Boolean, PropertyDrawEntity> relativePosition = getRelativePosition(commonOptions);

                    LSFFormPropertiesNamesDeclList formPropertiesNamesDeclList = mappedProps.getFormPropertiesNamesDeclList();

                    List<LSFFormPropertyDrawNameDecl> formPropertyDrawNameDeclList;
                    boolean reverseFor = relativePosition != null && !relativePosition.first;
                    if (formPropertiesNamesDeclList != null) {
                        formPropertyDrawNameDeclList = formPropertiesNamesDeclList.getFormPropertyDrawNameDeclList();
                        for (int i = reverseFor ? formPropertyDrawNameDeclList.size() - 1 : 0; (reverseFor && i >= 0) || (!reverseFor && i < formPropertyDrawNameDeclList.size()); i = i + (reverseFor ? -1 : 1)) {
                            LSFFormPropertyDrawNameDecl prop = formPropertyDrawNameDeclList.get(i);
                            String alias = prop.getSimpleName() != null ? prop.getSimpleName().getName() : null;
                            addPropertyWithOptions(alias, prop.getFormPropertyName(), commonOptions, prop.getFormPropertyOptionsList(), relativePosition, objectUsageList);
                        }
                    }
                }
            }
            PsiTreeUtil.findChildrenOfType(formProperties, LSFPropertyDrawDeclaration.class);
        }
    }

    private void addPropertyWithOptions(String alias, LSFFormPropertyName formPropertyName, LSFFormPropertyOptionsList commonOptions,
                                        LSFFormPropertyOptionsList formPropertyOptions, Pair<Boolean, PropertyDrawEntity> relativeProsition, LSFObjectUsageList objectUsageList) {
        LSFPropReference propUsage = formPropertyName.getPropertyUsage();

        List<ObjectEntity> objects = new ArrayList<ObjectEntity>();
        LSFNonEmptyObjectUsageList nonEmptyObjectUsageList = objectUsageList.getNonEmptyObjectUsageList();
        if (nonEmptyObjectUsageList != null) {
            for (LSFObjectUsage objectUsage : nonEmptyObjectUsageList.getObjectUsageList()) {
                objects.add(getObject(objectUsage.getNameRef()));
            }
        }
        GroupObjectEntity groupObject = getApplyObject(objects);

        PropertyDrawEntity propertyDraw = null;
        if (propUsage != null) {
            LSFPropDeclaration propDeclaration = propUsage.resolveDecl();
            propertyDraw = new PropertyDrawEntity(alias, propUsage.getNameRef(), objects, propDeclaration, commonOptions, formPropertyOptions, this);
        } else {
            LSFPredefinedFormPropertyName predef = formPropertyName.getPredefinedFormPropertyName();
            if (predef != null) {
                String name = predef.getName();
                if ("OBJVALUE".equals(name)) {
                    propertyDraw = new ObjectValueProperty(alias, groupObject, commonOptions, formPropertyOptions, this);
                    propertyDraw.baseClass = new ObjectClass();
                } else if ("SELECTION".equals(name)) {
                    String sIDPostfix = "";
                    for (int i = 0; i < objects.size(); i++) {
                        sIDPostfix += objects.get(i).valueClass.getName();
                        if (i + 1 < objects.size()) {
                            sIDPostfix += '|';
                        }
                    }

                    propertyDraw = new SelectionProperty(alias, sIDPostfix, commonOptions, formPropertyOptions, this);
                    RegularFilterGroupEntity filterGroup = new RegularFilterGroupEntity(null);
                    RegularFilterEntity filter = new RegularFilterEntity("Отмеченные", KeyStrokes.getSelectionFilterKeyStroke(), objects);
                    filterGroup.addFilter(filter);
                    regularFilterGroups.add(filterGroup);
                } else if ("ADDOBJ".equals(name)) {
                    propertyDraw = new AddObjectActionProperty(alias, groupObject, null, commonOptions, formPropertyOptions, this);
                } else if ("ADDFORM".equals(name) || "ADDSESSIONFORM".equals(name)) {
                    propertyDraw = new AddFormAction(alias, groupObject, commonOptions, formPropertyOptions, this, "ADDSESSIONFORM".equals(name));
                } else if ("EDITFORM".equals(name) || "EDITSESSIONFORM".equals(name)) {
                    propertyDraw = new EditFormAction(alias, groupObject, commonOptions, formPropertyOptions, this, "EDITSESSIONFORM".equals(name));
                } else if ("DELETE".equals(name) || "DELETESESSION".equals(name)) {
                    propertyDraw = new DeleteAction(alias, groupObject, commonOptions, formPropertyOptions, this, "DELETESESSION".equals(name));
                }
            }
        }

        if (propertyDraw != null) {
            addPropertyDraw(propertyDraw,
                    relativeProsition == null ? getRelativePosition(formPropertyOptions) : relativeProsition);
            if (groupObject != null && propertyDraw.toDraw == null) {
                propertyDraw.toDraw = groupObject;
            }
        }
    }

    private Pair<Boolean, PropertyDrawEntity> getRelativePosition(LSFFormPropertyOptionsList options) {
        if (options != null) {
            List<LSFFormOptionsWithFormPropertyDraw> formOptionsWithFormPropertyDrawList = options.getFormOptionsWithFormPropertyDrawList();
            if (!formOptionsWithFormPropertyDrawList.isEmpty()) {
                LSFFormOptionsWithFormPropertyDraw beforeAfterOption = formOptionsWithFormPropertyDrawList.get(formOptionsWithFormPropertyDrawList.size() - 1);
                String formPropertyName = beforeAfterOption.getFormPropertyDrawUsage().getNameRef();
                if (beforeAfterOption.getText().startsWith("BEFORE")) {
                    return new Pair<Boolean, PropertyDrawEntity>(true, getPropertyDraw(formPropertyName));
                } else if (beforeAfterOption.getText().startsWith("AFTER")) {
                    return new Pair<Boolean, PropertyDrawEntity>(false, getPropertyDraw(formPropertyName));
                }
            }
        }
        return null;
    }

    private void addTreeGroupObject(LSFFormTreeGroupObjectList tgobjDecl) {
        LSFTreeGroupDeclaration treeGroupDeclaration = tgobjDecl.getTreeGroupDeclaration();
        String sID = null;
        if (treeGroupDeclaration != null) {
            sID = treeGroupDeclaration.getDeclName();
        }
        TreeGroupEntity treeGroup = new TreeGroupEntity(sID);
        treeGroups.add(treeGroup);

        for (LSFFormTreeGroupObjectDeclaration treeGroupObjectDeclaration : tgobjDecl.getFormTreeGroupObjectDeclarationList()) {
            GroupObjectEntity groupObjectEntity = addGroupObject(treeGroupObjectDeclaration.getFormCommonGroupObject(), null, null);
            treeGroup.addGroupObject(groupObjectEntity);

            LSFTreeGroupParentDeclaration parentDeclaration = treeGroupObjectDeclaration.getTreeGroupParentDeclaration();
            if (parentDeclaration != null) {
                List<LSFPropertyUsage> properties = new ArrayList<LSFPropertyUsage>();
                if (parentDeclaration.getPropertyUsage() != null) {
                    properties.add(parentDeclaration.getPropertyUsage());
                } else {
                    properties.addAll(parentDeclaration.getNonEmptyPropertyUsageList().getPropertyUsageList());
                }
                // todo setIsParents?
            }

        }

    }

    private void addPropertyDraw(PropertyDrawEntity propertyDraw, Pair<Boolean, PropertyDrawEntity> relative) {
        if (relative != null && propertyDraws.contains(relative.second)) {
            propertyDraws.remove(propertyDraw);
            propertyDraws.add(propertyDraws.indexOf(relative.second) + (relative.first ? 0 : 1), propertyDraw);
        } else {
            propertyDraws.add(propertyDraw);
        }
    }

    public GroupObjectEntity getGroupObject(String sID) {
        if (sID != null) {
            for (GroupObjectEntity groupObject : groups) {
                if (sID.equals(groupObject.sID)) {
                    return groupObject;
                }
            }
        }
        return null;
    }

    public ObjectEntity getObject(String sID) {
        if (sID != null) {
            for (GroupObjectEntity groupObject : groups) {
                for (ObjectEntity object : groupObject.objects) {
                    if (sID.equals(object.sID)) {
                        return object;
                    }
                }
            }
        }
        return null;
    }

    public GroupObjectEntity getApplyObject(Collection<ObjectEntity> objects) {
        GroupObjectEntity result = null;
        for (GroupObjectEntity group : groups) {
            for (ObjectEntity object : group.objects) {
                if (objects.contains(object)) {
                    result = group;
                    break;
                }
            }
        }
        return result;
    }

    public PropertyDrawEntity getPropertyDraw(String sID) {
        if (sID != null) {
            for (PropertyDrawEntity property : propertyDraws) {
                if (sID.equals(property.sID)) {
                    return property;
                }
            }
        }
        return null;
    }

    private void initFormButtons() {
        addPropertyDraw(printActionPropertyDraw = addFormButton("formPrint"), null);
        addPropertyDraw(editActionPropertyDraw = addFormButton("formEdit"), null);
        addPropertyDraw(xlsActionPropertyDraw = addFormButton("formXls"), null);
        addPropertyDraw(dropActionPropertyDraw = addFormButton("formDrop"), null);
        addPropertyDraw(refreshActionPropertyDraw = addFormButton("formRefresh"), null);
        addPropertyDraw(applyActionPropertyDraw = addFormButton("formApply"), null);
        addPropertyDraw(cancelActionPropertyDraw = addFormButton("formCancel"), null);
        addPropertyDraw(okActionPropertyDraw = addFormButton("formOk"), null);
        addPropertyDraw(closeActionPropertyDraw = addFormButton("formClose"), null);
    }

    private PropertyDrawEntity addFormButton(String sID) {
        LSFGlobalPropDeclaration declaration = null;
        Collection<LSFGlobalPropDeclaration> propDeclarations = PropIndex.getInstance().get(sID, getProject(), file.getRequireScope());
        if (!propDeclarations.isEmpty()) {
            declaration = propDeclarations.iterator().next();
        }
        return new PropertyDrawEntity(sID, declaration, null, null, this);
    }

    public boolean isModal() {
        return modalityType.isModal();
    }

    public boolean isDialog() {
        return modalityType.isDialog();
    }
}
