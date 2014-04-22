package com.lsfusion.design.model.entity;

import com.intellij.openapi.project.Project;
import com.lsfusion.design.model.AbstractGroup;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGroupDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDrawEntity {

    public AbstractGroup parent;

    public String caption;

    public String propertyName;

    public String sID;
    public boolean toolbar = false;
    public ClassViewType forceViewType = null;
    public GroupObjectEntity toDraw;

    public boolean isAction;

    public LSFClassSet baseClass;

    public Project project;

    public boolean askConfirm;
    public String askConfirmMessage;

    public String declText;
    public String declLocation;
    public List<String> interfeceClasses = new ArrayList<String>();
    public List<String> objectClasses = new ArrayList<String>();

    // следующие параметры берутся из объявления свойства и используются в качестве значений по умолчанию в дизайне 
    public int fixedCharWidth;
    public int minimumCharWidth;
    public int maximumCharWidth;
    public int preferredCharWidth;
    public String iconPath;
    public KeyStroke editKey;
    public boolean showEditKey = true;

    public PropertyDrawEntity(String sID, LSFPropDeclaration propDeclaration, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        this(sID, null, new ArrayList<ObjectEntity>(), propDeclaration, commonFormOptions, propertyFormOptions, form);
    }

    public PropertyDrawEntity(String alias, String propertyName, List<ObjectEntity> objects, LSFPropDeclaration propDeclaration, LSFFormPropertyOptionsList commonFormOptions, LSFFormPropertyOptionsList propertyFormOptions, FormEntity form) {
        if (alias != null) {
            sID = alias;
        } else {
            sID = propertyName;
            if (!objects.isEmpty()) {
                sID += "_";
                for (ObjectEntity obj : objects) {
                    sID += obj.sID;
                    if (objects.indexOf(obj) < objects.size() - 1) {
                        sID += "_";
                    }
                }
            }
        }
        this.propertyName = propertyName;

        project = form.getProject();

        for (ObjectEntity obj : objects) {
            objectClasses.add(obj.getCaption());
        }

        LSFPropertyOptions propertyOptions = null;
        if (propDeclaration != null) {
            propertyOptions = ((LSFPropertyStatement) propDeclaration).getPropertyOptions();
            caption = ((LSFGlobalPropDeclaration) propDeclaration).getCaption();
//            if (caption == null) {
//                caption = propDeclaration.getDeclName();
//            }
            isAction = ((LSFGlobalPropDeclaration) propDeclaration).isAction();
            baseClass = propDeclaration.resolveValueClass(true);

            declText = propDeclaration.getText();
            declLocation = propDeclaration.getLocationString();
            List<LSFClassSet> paramClasses = propDeclaration.resolveParamClasses();
            if (paramClasses != null) {
                for (LSFClassSet classSet : paramClasses) {
                    if (classSet instanceof DataClass) {
                        interfeceClasses.add(((DataClass) classSet).getCaption());
                    } else if (classSet != null) {
                        interfeceClasses.add(classSet.toString());
                    }
                }
            }
        }

        if (propertyOptions != null) {
            toolbar = !propertyOptions.getToolbarSettingList().isEmpty();

            List<LSFFixedCharWidthSetting> fixedCharWidthSettings = propertyOptions.getFixedCharWidthSettingList();
            if (!fixedCharWidthSettings.isEmpty()) {
                fixedCharWidth = Integer.parseInt(fixedCharWidthSettings.get(fixedCharWidthSettings.size() - 1).getIntLiteral().getText());
            }

            List<LSFMinCharWidthSetting> minCharWidthSettings = propertyOptions.getMinCharWidthSettingList();
            if (!minCharWidthSettings.isEmpty()) {
                minimumCharWidth = Integer.parseInt(minCharWidthSettings.get(minCharWidthSettings.size() - 1).getIntLiteral().getText());
            }

            List<LSFMaxCharWidthSetting> maxCharWidthSettings = propertyOptions.getMaxCharWidthSettingList();
            if (!maxCharWidthSettings.isEmpty()) {
                maximumCharWidth = Integer.parseInt(maxCharWidthSettings.get(maxCharWidthSettings.size() - 1).getIntLiteral().getText());
            }

            List<LSFPrefCharWidthSetting> prefCharWidthSettings = propertyOptions.getPrefCharWidthSettingList();
            if (!prefCharWidthSettings.isEmpty()) {
                preferredCharWidth = Integer.parseInt(prefCharWidthSettings.get(prefCharWidthSettings.size() - 1).getIntLiteral().getText());
            }

            List<LSFImageSetting> imageSettings = propertyOptions.getImageSettingList();
            if (!imageSettings.isEmpty()) {
                iconPath = imageSettings.get(imageSettings.size() - 1).getStringLiteral().getValue();
            }

            List<LSFEditKeySetting> editKeySettings = propertyOptions.getEditKeySettingList();
            if (!editKeySettings.isEmpty()) {
                LSFEditKeySetting editKeySetting = editKeySettings.get(editKeySettings.size() - 1);
                editKey = KeyStroke.getKeyStroke(editKeySetting.getStringLiteral().getValue());
                if (editKeySetting.getHideEditKey() != null) {
                    showEditKey = false;
                }
            }

            getAbstractGroup(propertyOptions);
        }

        applyFormOptions(commonFormOptions, form);
        applyFormOptions(propertyFormOptions, form);
    }

    private void getAbstractGroup(LSFPropertyOptions propertyOptions) {
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
            String name = toDrawOption.getGroupObjectUsage().getNameRef();
            this.toDraw = form.getGroupObject(name);
        }
        List<LSFFormOptionForce> formOptionForceList = optionList.getFormOptionForceList();
        if (!formOptionForceList.isEmpty()) {
            LSFFormOptionForce forceOption = formOptionForceList.get(formOptionForceList.size() - 1);
            String forceText = forceOption.getClassViewType().getText();
            forceViewType = ClassViewType.valueOf(forceText);
        }
        List<LSFFormOptionToolbar> formOptionToolbarList = optionList.getFormOptionToolbarList();
        if (!formOptionToolbarList.isEmpty()) {
            toolbar = true;
        }
    }

    public GroupObjectEntity getToDraw(FormEntity form) {
        return toDraw;
    }

    public String getCaption() {
        return caption;
    }
}
