package com.lsfusion.design.model.entity;

import com.intellij.openapi.project.Project;
import com.lsfusion.design.FormView;
import com.lsfusion.design.model.AbstractGroup;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFGroupDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.psi.impl.LSFPropertyStatementImpl;
import com.lsfusion.refactoring.PropertyCanonicalNameUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.lsfusion.design.ui.ClassViewType.valueOf;

public class PropertyDrawEntity {
    
    protected void initDefaultView() {        
    }
    
    public AbstractGroup parent;

    public String caption;

    public String propertyName;

    public String sID;

    public boolean isToolbar(FormEntity entity) {
        if(forceViewType != null)
            return forceViewType.isToolbar();
        
        GroupObjectEntity toDraw = getToDraw(entity);
        return toDraw != null && toDraw.initClassView.isToolbar();
    }

    public boolean isGrid(FormEntity entity) {
        GroupObjectEntity toDraw = getToDraw(entity);
        return toDraw != null && toDraw.initClassView.isGrid() && (forceViewType == null || forceViewType.isGrid());
    }
    
    public ClassViewType forceViewType = null;
    public GroupObjectEntity toDraw;

    public boolean isAction;

    public LSFClassSet baseClass;

    public Project project;

    public boolean askConfirm;
    public String askConfirmMessage;

    public String canonicalName = "";
    public String declText;
    public String declLocation;
    public List<String> interfeceClasses = new ArrayList<>();
    public List<String> objectClasses = new ArrayList<>();

    // следующие параметры берутся из объявления свойства и используются в качестве значений по умолчанию в дизайне 
    public int fixedCharWidth;
    public int charWidth;
    public String iconPath;
    public KeyStroke changeKey;
    public boolean showChangeKey = true;

    public PropertyDrawEntity(String alias, String propertyName, List<ObjectEntity> objects, LSFPropDeclaration propDeclaration, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        List<String> objectNames = new ArrayList<>();
        if(alias == null) {
            for (ObjectEntity obj : objects) {
                if (obj != null) {
                    objectNames.add(obj.sID);
                }
            }
        }
        sID = FormView.getPropertyDrawName(alias, propertyName, objectNames);
        this.propertyName = propertyName;
        
        initDefaultView();

        project = form.getProject();

        for (ObjectEntity obj : objects) {
            if (obj != null) {
                objectClasses.add(obj.getCaption());
            }
        }

        LSFNonEmptyPropertyOptions propertyOptions = null;
        if (propDeclaration != null) {
            LSFPropertyStatementImpl propertyStatement = (LSFPropertyStatementImpl) propDeclaration;
            if(propertyStatement.getPropertyCalcStatement() != null)
                forceViewType = ClassViewType.GRID;
            else
                forceViewType = ClassViewType.PANEL;
                
            propertyOptions = propertyStatement.getNonEmptyPropertyOptions();
            caption = propertyStatement.getCaption();
//            if (caption == null) {
//                caption = propertyStatement.getDeclName();
//            }
            isAction = propertyStatement.isAction();
            baseClass = propertyStatement.resolveValueClass();

            declText = propertyStatement.getText();
            declLocation = propertyStatement.getLocationString();
            List<LSFClassSet> paramClasses = propertyStatement.resolveParamClasses();
            if (paramClasses != null) {
                for (LSFClassSet classSet : paramClasses) {
                    if (classSet instanceof DataClass) {
                        interfeceClasses.add(((DataClass) classSet).getCaption());
                    } else if (classSet != null) {
                        interfeceClasses.add(classSet.toString());
                    }
                }
                canonicalName = PropertyCanonicalNameUtils.createName(propertyStatement.getNamespaceName(), propertyStatement.getGlobalName(), paramClasses);
            }
        }

        if (propertyOptions != null) {
            for(LSFViewTypeSetting viewType : propertyOptions.getViewTypeSettingList())
                forceViewType = valueOf(viewType.getClassViewType().getText());

            List<LSFFixedCharWidthSetting> fixedCharWidthSettings = propertyOptions.getFixedCharWidthSettingList();
            if (!fixedCharWidthSettings.isEmpty()) {
                fixedCharWidth = Integer.parseInt(fixedCharWidthSettings.get(fixedCharWidthSettings.size() - 1).getIntLiteral().getText());
            }

            List<LSFCharWidthSetting> minCharWidthSettings = propertyOptions.getCharWidthSettingList();
            if (!minCharWidthSettings.isEmpty()) {
                charWidth = Integer.parseInt(minCharWidthSettings.get(minCharWidthSettings.size() - 1).getIntLiteral().getText());
            }

            List<LSFImageSetting> imageSettings = propertyOptions.getImageSettingList();
            if (!imageSettings.isEmpty()) {
                iconPath = imageSettings.get(imageSettings.size() - 1).getStringLiteral().getValue();
            }

            List<LSFEditKeySetting> editKeySettings = propertyOptions.getEditKeySettingList();
            if (!editKeySettings.isEmpty()) {
                LSFEditKeySetting editKeySetting = editKeySettings.get(editKeySettings.size() - 1);
                changeKey = KeyStroke.getKeyStroke(editKeySetting.getStringLiteral().getValue());
                if (editKeySetting.getHideEditKey() != null) {
                    showChangeKey = false;
                }
            }

            getAbstractGroup(propertyOptions);
        }

        applyFormOptions(commonFormOptions, form);
        applyFormOptions(propertyFormOptions, form);
    }

    private void getAbstractGroup(LSFNonEmptyPropertyOptions propertyOptions) {
        List<LSFGroupUsage> groupUsageList = propertyOptions.getGroupUsageList();
        if (!groupUsageList.isEmpty()) {
            LSFGroupUsage groupUsage = groupUsageList.get(groupUsageList.size() - 1);
            parent = addAbstractGroup(groupUsage);
        }
    }

    private AbstractGroup addAbstractGroup(LSFGroupUsage groupUsage) {
        LSFGroupDeclaration groupDeclaration = groupUsage.resolveDecl();
        if (groupDeclaration != null) {
            AbstractGroup group = new AbstractGroup(groupDeclaration.getName(), groupDeclaration.getCaption());
            LSFGroupUsage usage = groupDeclaration.getGroupUsage();
            if (usage != null) {
                if (!groupDeclaration.equals(usage.resolveDecl())) {
                    group.parent = addAbstractGroup(usage);
                }
            }
            return group;
        }
        return null;
    }

    private void applyFormOptions(LSFFormPropertyOptionsList optionList, FormEntity form) {
        if (optionList == null) {
            return;
        }
        List<LSFFormOptionToDraw> formOptionToDrawList = optionList.getFormOptionToDrawList();
        if (!formOptionToDrawList.isEmpty()) {
            LSFFormOptionToDraw toDrawOption = formOptionToDrawList.get(formOptionToDrawList.size() - 1);
            if (toDrawOption.getGroupObjectUsage() != null) {
                String name = toDrawOption.getGroupObjectUsage().getNameRef();
                this.toDraw = form.getGroupObject(name);
            }
        }
        List<LSFFormOptionForce> formOptionForceList = optionList.getFormOptionForceList();
        if (!formOptionForceList.isEmpty()) {
            LSFFormOptionForce forceOption = formOptionForceList.get(formOptionForceList.size() - 1);
            if (forceOption.getClassViewType() != null) {
                String forceText = forceOption.getClassViewType().getText();
                forceViewType = ClassViewType.valueOf(forceText);
            }
        }
    }

    public GroupObjectEntity getToDraw(FormEntity form) {
        return toDraw;
    }

    public String getCaption() {
        return caption;
    }
}
