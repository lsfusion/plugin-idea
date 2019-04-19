package com.lsfusion.design.model.entity;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.design.FormView;
import com.lsfusion.design.model.ModalityType;
import com.lsfusion.design.model.property.*;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.ObjectClass;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.indexes.ActionIndex;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class FormEntity {
    private LSFFile file;

    public List<GroupObjectEntity> groups = new ArrayList<>();
    public List<TreeGroupEntity> treeGroups = new ArrayList<>();
    public List<PropertyDrawEntity> propertyDraws = new ArrayList<>();
    public List<RegularFilterGroupEntity> regularFilterGroups = new ArrayList<>();

    public PropertyDrawEntity editActionPropertyDraw;
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
        }

        List<PsiElement> sortedDecls = new ArrayList<>();
        sortedDecls.addAll(formExtend.getFormGroupObjectDeclarations());
        sortedDecls.addAll(formExtend.getFormTreeGroupObjectListList());
        sortedDecls.sort(Comparator.comparingInt(PsiElement::getTextOffset));
        for (PsiElement element : sortedDecls) {
            if(element instanceof LSFFormGroupObjectDeclaration) {
                LSFFormGroupObjectDeclaration formGroupObjectDeclaration = (LSFFormGroupObjectDeclaration) element;
                LSFFormGroupObjectViewType viewType = BaseUtils.last(formGroupObjectDeclaration.getFormGroupObjectOptions().getFormGroupObjectViewTypeList());
                LSFFormGroupObjectPageSize pageSize = BaseUtils.last(formGroupObjectDeclaration.getFormGroupObjectOptions().getFormGroupObjectPageSizeList());
                LSFFormGroupObjectRelativePosition neighbour = BaseUtils.last(formGroupObjectDeclaration.getFormGroupObjectOptions().getFormGroupObjectRelativePositionList());
                addGroupObject(formGroupObjectDeclaration.getFormCommonGroupObject(), neighbour, viewType, pageSize);
            } else {
                LSFFormTreeGroupObjectList treeGroupObjectDeclaration = (LSFFormTreeGroupObjectList) element;
                LSFFormGroupObjectRelativePosition neighbour = BaseUtils.last(treeGroupObjectDeclaration.getFormTreeGroupObjectOptions().getFormGroupObjectRelativePositionList());
                addTreeGroupObject(treeGroupObjectDeclaration, neighbour);
            }
        }

        extractPropertyDraws(formExtend);

        for (LSFFormFilterGroupDeclaration lsfFormFilterGroupDeclaration : formExtend.getFormFilterGroupDeclarationList()) {
            addRegularFilterGroup(lsfFormFilterGroupDeclaration);
        }

        for (LSFFormExtendFilterGroupDeclaration lsfFormExtendFilterGroupDeclaration : formExtend.getFormExtendFilterGroupDeclarationList()) {
            extendRegularFilterGroup(lsfFormExtendFilterGroupDeclaration);
        }
    }

    private GroupObjectEntity addGroupObject(LSFGroupObjectDeclaration gobjDecl, LSFFormGroupObjectRelativePosition neighbour, LSFFormGroupObjectViewType viewType, LSFFormGroupObjectPageSize pageSize) {
        GroupObjectEntity neighbourGroupObject = getGroupObject(neighbour);
        boolean isRight = neighbourGroupObject != null ? LSFPsiImplUtil.isRight(neighbour) : false;
        return addGroupObject(gobjDecl, neighbourGroupObject, isRight, viewType, pageSize);
    }

    private GroupObjectEntity getGroupObject(@Nullable LSFFormGroupObjectRelativePosition neighbour) {
        if(neighbour != null) {
            GroupObjectEntity neighbourGroupObject = getGroupObject(neighbour.getGroupObjectUsage());
            if(neighbourGroupObject != null && neighbourGroupObject.isInTree()) { // later check this in reference annotator (as an error) 
                boolean isRight = LSFPsiImplUtil.isRight(neighbour);
                if(isRight) {
                    if(!neighbourGroupObject.equals(BaseUtils.last(neighbourGroupObject.treeGroup.groups)))
                        return null;
                } else {
                    if(!neighbourGroupObject.equals(neighbourGroupObject.treeGroup.groups.get(0)))
                        return null;
                }
            }
            return neighbourGroupObject;
        }
        return null;
    }

    private GroupObjectEntity getGroupObject(@Nullable LSFGroupObjectUsage groupObjectUsage) {
        if(groupObjectUsage != null)
            return getGroupObject(groupObjectUsage.getNameRef());
        return null;
    }

    private GroupObjectEntity addGroupObject(LSFGroupObjectDeclaration gobjDecl, GroupObjectEntity neighbour, boolean isRight, LSFFormGroupObjectViewType viewType, LSFFormGroupObjectPageSize pageSize) {
        GroupObjectEntity groupObject = new GroupObjectEntity(gobjDecl.getDeclName(), viewType, pageSize);
        Collection<LSFFormObjectDeclaration> objectDecarations = gobjDecl.findObjectDecarations();
        for (LSFFormObjectDeclaration objDecl : objectDecarations) {
            LSFClassSet objectClass = objDecl.resolveClass();
            if (objectClass != null) {
                groupObject.add(new ObjectEntity(objDecl.getDeclName(), objectClass.getCommonClass()));
            }
        }
        if (!groupObject.objects.isEmpty()) {
            if(neighbour != null) {
                int neighbourIndex = groups.indexOf(neighbour);
                if(isRight)
                    neighbourIndex++;
                groups.add(neighbourIndex, groupObject);
            } else {
                groups.add(groupObject);
            }
        }

        return groupObject;
    }

    private void addRegularFilterGroup(LSFFormFilterGroupDeclaration filterGroupDeclaration) {
        LSFFilterGroupName filterGroupName = filterGroupDeclaration.getFilterGroupName();
        if (filterGroupName == null) {
            return;
        }
        RegularFilterGroupEntity filterGroup = new RegularFilterGroupEntity(filterGroupName.getSimpleName().getName());
        
        addFiltersToFilterGroup(filterGroup, filterGroupDeclaration.getRegularFilterDeclarationList());
        
        regularFilterGroups.add(filterGroup);
    }
    
    private void extendRegularFilterGroup(LSFFormExtendFilterGroupDeclaration filterGroupExtend) {
        LSFFilterGroupUsage filterGroupUsage = filterGroupExtend.getFilterGroupUsage();
        if (filterGroupUsage != null) {
            LSFFilterGroupDeclaration filterGroupDecl = filterGroupUsage.resolveDecl();
            if (filterGroupDecl != null) {
                RegularFilterGroupEntity filterGroup = getFilterGroup(filterGroupDecl.getSimpleName().getName());
                if (filterGroup != null) {
                    addFiltersToFilterGroup(filterGroup, filterGroupExtend.getRegularFilterDeclarationList());
                }
            }
        }
    }

    private void addFiltersToFilterGroup(RegularFilterGroupEntity filterGroup, List<LSFRegularFilterDeclaration> regularFilterDeclarationList) {
        for (LSFRegularFilterDeclaration regularFilterDeclaration : regularFilterDeclarationList) {

            LSFFormExprDeclaration formExprDeclaration = regularFilterDeclaration.getFormExprDeclaration();
            Set<ObjectEntity> params = getObjects(formExprDeclaration);

            LSFStringLiteral stringLiteral = regularFilterDeclaration.getStringLiteral();
            boolean isDefault = regularFilterDeclaration.getFilterSetDefault() != null;
            KeyStroke keyStroke = null;
            if (stringLiteral != null) {
                keyStroke = KeyStroke.getKeyStroke(stringLiteral.getValue());
            }
            RegularFilterEntity filter = new RegularFilterEntity(regularFilterDeclaration.getLocalizedStringLiteral().getValue(), keyStroke, params, isDefault);
            filterGroup.addFilter(filter);
        }
    }

    private Set<ObjectEntity> getObjects(LSFFormExprDeclaration formExprDeclaration) {
        Set<ObjectEntity> params = new HashSet<>();
        for (String paramDeclaration : formExprDeclaration.getPropertyExpression().resolveAllParams()) {
            params.add(getObject(paramDeclaration));
        }
        return params;
    }

    private Set<ObjectEntity> getObjects(LSFFormActionDeclaration formActionDeclaration) {
        Set<ObjectEntity> params = new HashSet<>();
        for (String paramDeclaration : formActionDeclaration.getListActionPropertyDefinitionBody().resolveAllParams()) {
            params.add(getObject(paramDeclaration));
        }
        return params;
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
                        String alias = prop.getSimpleName() != null ? prop.getSimpleName().getName() : null;
                        
                        boolean isAction = false;
                        LSFClassSet valueClass = null;

                        List<ObjectEntity> objectUsageList;
                        LSFFormPropertyDrawObject formPropertyObject = prop.getFormPropertyDrawObject();
                        LSFFormPropertyName formPropertyName = null;
                        if(formPropertyObject != null) {
                            formPropertyName = formPropertyObject.getFormPropertyName();
                            objectUsageList = getObjects(formPropertyObject.getObjectUsageList());
                        } else {
                            LSFFormExprDeclaration formExprDeclaration = prop.getFormExprDeclaration();
                            if(formExprDeclaration != null) {
                                valueClass = LSFExClassSet.fromEx(formExprDeclaration.getPropertyExpression().resolveValueClass(false));
                                objectUsageList = new ArrayList<>(getObjects(formExprDeclaration));
                            } else {
                                isAction = true;
                                objectUsageList = new ArrayList<>(getObjects(prop.getFormActionDeclaration()));
                            }
                        }
                        addPropertyWithOptions(alias, prop.getLocalizedStringLiteral(), formPropertyName, isAction, valueClass, commonOptions, prop.getFormPropertyOptionsList(), relativeProsition, objectUsageList);
                    }
                }
            } else {
                LSFFormMappedNamePropertiesList mappedProps = formProperties.getFormMappedNamePropertiesList();
                if (mappedProps != null) {
                    List<ObjectEntity> objectUsageList = getObjects(mappedProps.getObjectUsageList());

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
                            addPropertyWithOptions(alias, prop.getLocalizedStringLiteral(), prop.getFormPropertyName(), false,  null, commonOptions, prop.getFormPropertyOptionsList(), relativePosition, objectUsageList);
                        }
                    }
                }
            }
            PsiTreeUtil.findChildrenOfType(formProperties, LSFPropertyDrawDeclaration.class);
        }
    }

    private void addPropertyWithOptions(String alias, LSFLocalizedStringLiteral captionLiteral, LSFFormPropertyName formPropertyName, boolean isAction, LSFClassSet valueClass, LSFFormPropertyOptionsList commonOptions,
                                        LSFFormPropertyOptionsList formPropertyOptions, Pair<Boolean, PropertyDrawEntity> relativeProsition, List<ObjectEntity> objects) {

        String caption = captionLiteral != null ? captionLiteral.getValue() : null;
        
        PropertyDrawEntity propertyDraw = null;
        GroupObjectEntity groupObject = getApplyObject(objects);
        
        if(formPropertyName != null) {
            LSFActionOrPropReference<?, ?> propUsage = formPropertyName.getPropertyElseActionUsage();
            if(propUsage == null)
                propUsage = formPropertyName.getActionUsage();

            if (propUsage != null) {
                LSFActionOrGlobalPropDeclaration propDeclaration = (LSFActionOrGlobalPropDeclaration)propUsage.resolveDecl();
                propertyDraw = new PropertyDrawEntity(alias, propUsage.getNameRef(), objects, propDeclaration, caption, commonOptions, formPropertyOptions, this);
            } else {
                LSFPredefinedFormPropertyName predefinedFormPropertyName = formPropertyName.getPredefinedFormPropertyName();
                if (predefinedFormPropertyName != null && groupObject != null) {
                    LSFPredefinedAddPropertyName predefinedAddPropertyName = predefinedFormPropertyName.getPredefinedAddPropertyName();
                    String name = predefinedAddPropertyName != null ? predefinedAddPropertyName.getText() : predefinedFormPropertyName.getName();
                    if ("VALUE".equals(name)) {
                        propertyDraw = new ObjectValueProperty(alias, caption, groupObject, commonOptions, formPropertyOptions, this);
                        propertyDraw.baseClass = new ObjectClass();
                    } else if ("NEW".equals(name)) {
                        propertyDraw = new AddObjectActionProperty(alias, caption, groupObject, commonOptions, formPropertyOptions, this);
                    } else if ("NEWEDIT".equals(name)) {
                        propertyDraw = new AddFormAction(alias, caption, groupObject, commonOptions, formPropertyOptions, this);
                    } else if ("EDIT".equals(name)) {
                        propertyDraw = new EditFormAction(alias, caption, groupObject, commonOptions, formPropertyOptions, this);
                    } else if ("DELETE".equals(name)) {
                        propertyDraw = new DeleteAction(alias, caption, groupObject, commonOptions, formPropertyOptions, this);
                    }
                }
            }
        } else // expr or listAction
            propertyDraw = new PropertyDrawEntity(alias, null, objects, isAction, caption, valueClass, commonOptions, formPropertyOptions, this);
        
        if (propertyDraw != null) {
            addPropertyDraw(propertyDraw,
                    relativeProsition == null ? getRelativePosition(formPropertyOptions) : relativeProsition);
            if (groupObject != null && propertyDraw.toDraw == null) {
                propertyDraw.toDraw = groupObject;
            }
        }

    }

    private List<ObjectEntity> getObjects(LSFObjectUsageList objectUsageList) {
        List<ObjectEntity> objects = new ArrayList<>();
        if (objectUsageList != null) {
            LSFNonEmptyObjectUsageList nonEmptyObjectUsageList = objectUsageList.getNonEmptyObjectUsageList();
            if (nonEmptyObjectUsageList != null) {
                for (LSFObjectUsage objectUsage : nonEmptyObjectUsageList.getObjectUsageList()) {
                    objects.add(getObject(objectUsage.getNameRef()));
                }
            }
        }
        return objects;
    }

    private Pair<Boolean, PropertyDrawEntity> getRelativePosition(LSFFormPropertyOptionsList options) {
        if (options != null) {
            List<LSFFormOptionsWithFormPropertyDraw> formOptionsWithFormPropertyDrawList = options.getFormOptionsWithFormPropertyDrawList();
            if (!formOptionsWithFormPropertyDrawList.isEmpty()) {
                LSFFormOptionsWithFormPropertyDraw beforeAfterOption = formOptionsWithFormPropertyDrawList.get(formOptionsWithFormPropertyDrawList.size() - 1);
                LSFFormPropertyDrawUsage formPropertyDrawUsage = beforeAfterOption.getFormPropertyDrawUsage();
                if(formPropertyDrawUsage != null) {
                    String formPropertyName = FormView.getPropertyDrawName(formPropertyDrawUsage);
                    if (beforeAfterOption.getText().startsWith("BEFORE")) {
                        return new Pair<>(true, getPropertyDraw(formPropertyName));
                    } else if (beforeAfterOption.getText().startsWith("AFTER")) {
                        return new Pair<>(false, getPropertyDraw(formPropertyName));
                    }
                }
            }
        }
        return null;
    }

    private void addTreeGroupObject(LSFFormTreeGroupObjectList tgobjDecl, LSFFormGroupObjectRelativePosition neighbour) {
        LSFTreeGroupDeclaration treeGroupDeclaration = tgobjDecl.getTreeGroupDeclaration();
        String sID = null;
        if (treeGroupDeclaration != null) {
            sID = treeGroupDeclaration.getDeclName();
        }
        TreeGroupEntity treeGroup = new TreeGroupEntity(sID);
        treeGroups.add(treeGroup);

        GroupObjectEntity neighbourGroupObject = getGroupObject(neighbour);
        boolean isRight = neighbourGroupObject != null ? LSFPsiImplUtil.isRight(neighbour) : false;

        List<LSFFormTreeGroupObjectDeclaration> gobjList = tgobjDecl.getFormTreeGroupObjectDeclarationList();
        for (LSFFormTreeGroupObjectDeclaration treeGroupObjectDeclaration : (neighbourGroupObject != null && !isRight ? BaseUtils.reverse(gobjList) : gobjList)) {
            GroupObjectEntity groupObjectEntity = addGroupObject(treeGroupObjectDeclaration.getFormCommonGroupObject(), neighbourGroupObject, isRight, null, null);
            if(neighbourGroupObject != null)
                neighbourGroupObject = groupObjectEntity;
            treeGroup.addGroupObject(groupObjectEntity);

            LSFTreeGroupParentDeclaration parentDeclaration = treeGroupObjectDeclaration.getTreeGroupParentDeclaration();
            if (parentDeclaration != null) {
                List<LSFPropertyUsage> properties = new ArrayList<>();
                if (parentDeclaration.getPropertyUsage() != null) {
                    properties.add(parentDeclaration.getPropertyUsage());
                } else {
                    if (parentDeclaration.getNonEmptyPropertyUsageList() != null) {
                        properties.addAll(parentDeclaration.getNonEmptyPropertyUsageList().getPropertyUsageList());
                    }
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

    @Nullable
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

    @Nullable
    public RegularFilterGroupEntity getFilterGroup(String sID) {
        if (sID != null) {
            for (RegularFilterGroupEntity filterGroup : regularFilterGroups) {
                if (sID.equals(filterGroup.sID)) {
                    return filterGroup;
                }
            }
        }
        return null;
    }

    @Nullable
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
        addPropertyDraw(editActionPropertyDraw = addFormButton("formEdit"), null);
        addPropertyDraw(dropActionPropertyDraw = addFormButton("formDrop"), null);
        addPropertyDraw(refreshActionPropertyDraw = addFormButton("formRefresh"), null);
        addPropertyDraw(applyActionPropertyDraw = addFormButton("formApply"), null);
        addPropertyDraw(cancelActionPropertyDraw = addFormButton("formCancel"), null);
        addPropertyDraw(okActionPropertyDraw = addFormButton("formOk"), null);
        addPropertyDraw(closeActionPropertyDraw = addFormButton("formClose"), null);
    }

    private PropertyDrawEntity addFormButton(String sID) {
        LSFActionDeclaration declaration = null;
        Collection<LSFActionDeclaration> propDeclarations = ActionIndex.getInstance().get(sID, getProject(), file.getRequireScope());
        if (!propDeclarations.isEmpty()) {
            declaration = propDeclarations.iterator().next();
        }
        return new PropertyDrawEntity(null, sID, new ArrayList<ObjectEntity>(), declaration, true, null, null, null, null, this);
    }

    public boolean isModal() {
        return modalityType.isModal();
    }

    public boolean isDialog() {
        return modalityType.isDialog();
    }
}
