package com.lsfusion.design;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import com.lsfusion.design.model.ComponentView;
import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.PropertyDrawView;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.proxy.ViewProxyUtil;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.DesignUtils;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignInfo {
    public final LSFFormDeclaration formDecl;

    public FormView formView;

    public DesignInfo(LSFFormDeclaration formDecl, LSFFile lsfFile, LSFLocalSearchScope localScope) {
        this.formDecl = formDecl;

        FormEntity formEntity = new FormEntity(lsfFile);

        List<LSFModuleDeclaration> requiredModules = DesignUtils.getRequiredModules(((LSFFile) formDecl.getContainingFile()).getModuleDeclaration());

        Query<LSFFormExtend> lsfFormExtends = LSFGlobalResolver.findExtendElements(formDecl, LSFStubElementTypes.EXTENDFORM, lsfFile, localScope);
        
        Map<PsiElement, LSFModuleDeclaration> elementToModule = new HashMap<>();
        for (LSFFormExtend formExtend : lsfFormExtends.findAll()) {
            LSFModuleDeclaration moduleDeclaration = formExtend.getLSFFile().getModuleDeclaration();
            if (moduleDeclaration != null) {
                elementToModule.put(formExtend, moduleDeclaration);
            }
        }

        List<PsiElement> sortedFormDecls = DesignUtils.sortByModules(requiredModules, elementToModule);
        for (PsiElement formExtend : sortedFormDecls) {
            formEntity.extendForm((LSFFormExtend) formExtend);
        }

        formView = DefaultFormView.create(formEntity);

        LSFId nameIdentifier = formDecl.getNameIdentifier();
        if(nameIdentifier != null) {
            Collection<PsiReference> refs = ReferencesSearch.search(nameIdentifier, lsfFile.getRequireScope()).findAll();

            elementToModule = new HashMap<>();
            for (PsiReference ref : refs) {
                if (ref instanceof LSFFormUsage) {
                    LSFFormUsage formUsage = (LSFFormUsage) ref;
                    if (formUsage.getParent() instanceof LSFDesignHeader) {
                        LSFModuleDeclaration moduleDeclaration = formUsage.getLSFFile().getModuleDeclaration();
                        if (moduleDeclaration != null) {
                            elementToModule.put(formUsage, moduleDeclaration);
                        }
                    }
                }
            }
        }
        
        for (PsiElement ref : DesignUtils.sortByModules(requiredModules, elementToModule)) {
            LSFDesignHeader designHeader = (LSFDesignHeader) ref.getParent();
            if (designHeader.getCustomFormDesignOption() != null) {
                formView = FormView.create(formEntity);
            }
            LSFDesignStatement designStatement = (LSFDesignStatement) designHeader.getParent();
            processComponentBody(formView.getMainContainer(), designStatement.getComponentBody());
        }
    }

    private void processComponentBody(ComponentView parentComponent, LSFComponentBody componentBody) {
        if (componentBody == null) {
            return;
        }
        LSFComponentBlockStatement componentBlockStatement = componentBody.getComponentBlockStatement();
        if (componentBlockStatement == null) {
            return;
        }

        List<LSFComponentStatement> componentStatements = componentBlockStatement.getComponentStatementList();

        for (LSFComponentStatement componentStatement : componentStatements) {
            LSFSetupComponentStatement setupComponentStatement = componentStatement.getSetupComponentStatement();
            if (setupComponentStatement != null) {
                String componentSID = getComponentSID(setupComponentStatement.getComponentSelector(), formView);
                ComponentView componentView = formView.getComponentBySID(componentSID);

                if (componentView != null) {
                    processComponentBody(componentView, setupComponentStatement.getComponentBody());
                }
            } else if (componentStatement.getNewComponentStatement() != null) {
                LSFNewComponentStatement statement = componentStatement.getNewComponentStatement();
                String name = statement.getComponentDecl().getName();
                ContainerView container = formView.createContainer(null, name);

                LSFComponentInsertPosition insertPositionSelector = statement.getComponentInsertPosition();
                if (parentComponent instanceof ContainerView && insertPositionSelector != null) {
                    addComponent(container, (ContainerView) parentComponent, insertPositionSelector, formView);
                }

                processComponentBody(container, statement.getComponentBody());
            } else if (componentStatement.getMoveComponentStatement() != null) {
                LSFMoveComponentStatement statement = componentStatement.getMoveComponentStatement();
                LSFComponentSelector componentSelector = statement.getComponentSelector();
                if (componentSelector != null) {
                    String name = getComponentSID(componentSelector, formView);
                    if (name != null) {
                        ComponentView component = formView.getComponentBySID(name);

                        if (component != null) {
                            if (!(component instanceof PropertyDrawView) || component.getContainer() != null) { // не добавляем свойства, которые уже добавлены в грид
                                LSFComponentInsertPosition insertPositionSelector = statement.getComponentInsertPosition();
                                if (parentComponent instanceof ContainerView && insertPositionSelector != null) {
                                    addComponent(component, (ContainerView) parentComponent, insertPositionSelector, formView);
                                }
                            }

                            processComponentBody(component, statement.getComponentBody());
                        }
                    }
                }
            } else if (componentStatement.getSetObjectPropertyStatement() != null) {
                LSFSetObjectPropertyStatement statement = componentStatement.getSetObjectPropertyStatement();
                String propertyName = statement.getFirstChild().getText();

                LSFComponentPropertyValue propertyValue = statement.getComponentPropertyValue();
                Object propertyValueObject = getPropertyValue(propertyValue);

                if (parentComponent != null) {
                    ViewProxyUtil.setObjectProperty(parentComponent.equals(formView.getMainContainer()) ? formView : parentComponent, propertyName, propertyValueObject);
                }
            } else if (componentStatement.getRemoveComponentStatement() != null) {
                LSFRemoveComponentStatement statement = componentStatement.getRemoveComponentStatement();
                LSFComponentSelector componentSelector = statement.getComponentSelector();
                if (componentSelector != null) {
                    String name = getComponentSID(componentSelector, formView);
                    ComponentView component = formView.getComponentBySID(name);
                    if (component != null) {
                        formView.removeComponent(component);
                    }
                }
            }
        }
    }

    private Object getPropertyValue(LSFComponentPropertyValue valueStatement) {
        if (valueStatement == null) {
            return null;
        }
        
        /*if (valueStatement.getColorLiteral() != null) {
            LSFColorLiteral colorLiteral = valueStatement.getColorLiteral();
            List<LSFUintLiteral> literalList = colorLiteral.getUintLiteralList();
            if (!literalList.isEmpty()) {
                //noinspection UseJBColor
                return new Color(Integer.decode(literalList.get(0).getText()), Integer.decode(literalList.get(1).getText()), Integer.decode(literalList.get(2).getText()));
            } else {
                return Color.decode(colorLiteral.getFirstChild().getText());
            }
        } else if (valueStatement.getLocalizedStringLiteral() != null) {
            return valueStatement.getLocalizedStringLiteral().getValue();
        } else if (valueStatement.getIntLiteral() != null) {
            return Integer.decode(valueStatement.getIntLiteral().getText());
        } else if (valueStatement.getDoubleLiteral() != null) {
            return Double.parseDouble(valueStatement.getDoubleLiteral().getText());
        } else */if (valueStatement.getDimensionLiteral() != null) {
            List<LSFIntLiteral> literalList = valueStatement.getDimensionLiteral().getIntLiteralList();
            return new Dimension(Integer.decode(literalList.get(0).getText()), Integer.decode(literalList.get(1).getText()));
        } else if (valueStatement.getBooleanLiteral() != null) {
            return "TRUE".equals(valueStatement.getBooleanLiteral().getText());
        } else if (valueStatement.getBoundsIntLiteral() != null) {
            return null; //??????????
        } else if (valueStatement.getBoundsDoubleLiteral() != null) {
            return null; //??????????
        } else if (valueStatement.getContainerTypeLiteral() != null) { //deprecated in 5.2, removed in 6.0
            return ContainerType.valueOf(valueStatement.getContainerTypeLiteral().getText());
        } else if (valueStatement.getFlexAlignmentLiteral() != null) {
            return FlexAlignment.valueOf(valueStatement.getFlexAlignmentLiteral().getText());
        } else if (valueStatement.getDesignCalcPropertyObject() != null) {
            return valueStatement.getDesignCalcPropertyObject().getFormCalcPropertyObject().getText();
        }
        return null;
    }

    private void addComponent(ComponentView component, ContainerView container, LSFComponentInsertPosition insertPositionSelector, FormView form) {
        LSFComponentSelector neighbour = insertPositionSelector.getComponentSelector();
        if (neighbour != null) {
            LSFInsertRelativePositionLiteral insertRelativePositionLiteral = insertPositionSelector.getInsertRelativePositionLiteral();
            if (insertRelativePositionLiteral != null) {
                String relativePosition = insertRelativePositionLiteral.getText();
                ComponentView componentView = form.getComponentBySID(getComponentSID(neighbour, form));
                if ("BEFORE".equals(relativePosition)) {
                    container.addBefore(component, componentView);
                } else {
                    container.addAfter(component, componentView);
                }
            }
        } else if ("FIRST".equals(insertPositionSelector.getText())) {
            container.addFirst(component);
        } else {
            container.add(component);
        }
    }

    private String getComponentSID(LSFComponentSelector componentSelector, FormView form) {
        LSFComponentSelector parentComponentSelector = componentSelector.getComponentSelector();
        if (parentComponentSelector != null) {
            String componentSID = getComponentSID(parentComponentSelector, form);
            ComponentView componentView = form.getComponentBySID(componentSID);
            return componentView.getContainer().getSID();
        } else if (componentSelector.getPropertySelector() != null) {
            return FormView.getPropertyDrawSID(FormView.getPropertyDrawName(componentSelector.getPropertySelector().getFormPropertyDrawUsage()));
        } else if (componentSelector.getFilterPropertySelector() != null) {
            return FormView.getFilterSID(FormView.getPropertyDrawName(componentSelector.getFilterPropertySelector().getPropertySelector().getFormPropertyDrawUsage()));
        } else if (componentSelector.getFilterGroupSelector() != null) {
            String name = componentSelector.getFilterGroupSelector().getFilterGroupUsage().getName();
            return FormView.getFilterGroupSID(name);
        } else if (componentSelector.getGroupObjectTreeSingleSelectorType() != null) {
            LSFGroupObjectTreeSingleSelectorType gos = componentSelector.getGroupObjectTreeSingleSelectorType();
            String groupName = getGroupObjectSID(componentSelector);
            if(groupName != null) {
                switch (gos.getText()) {
                    case "TOOLBARSYSTEM" : 
                        return FormView.getToolbarSystemSID(groupName);
                    case "FILTERBOX": 
                        return DefaultFormView.getFilterBoxContainerSID(groupName);
                    case "FILTERCONTROLS": 
                        return FormView.getFilterControlsComponentSID(groupName);
                    case "FILTERGROUPS" : 
                        return DefaultFormView.getFilterGroupsContainerSID(groupName);
                    case "FILTERS" :
                    case "USERFILTER" : // deprecated in 5.2, removed in 6.0
                        return FormView.getFiltersContainerSID(groupName);
                    case "CLASSCHOOSER" : 
                        return FormView.getClassChooserSID(groupName);
                    case "GRID" :
                    case "GRIDBOX": // backward compatibility
                        return FormView.getGridSID(groupName);
                    case "BOX" :
                        return DefaultFormView.getBoxContainerSID(groupName);
                    case "PANEL" : 
                        return DefaultFormView.getPanelContainerSID(groupName);
                    case "TOOLBARBOX" : 
                        return DefaultFormView.getToolbarBoxContainerSID(groupName);
                    case "TOOLBARLEFT" : 
                        return DefaultFormView.getToolbarLeftContainerSID(groupName);
                    case "TOOLBARRIGHT" : 
                        return DefaultFormView.getToolbarRightContainerSID(groupName);
                    case "TOOLBAR" : 
                        return DefaultFormView.getToolbarContainerSID(groupName);
                }
            }
        } else if (componentSelector.getGlobalSingleSelectorType() != null) {
            switch (componentSelector.getGlobalSingleSelectorType().getText()) {
                case "BOX":
                    return FormView.getBoxContainerSID();
                case "OBJECTS": 
                    return DefaultFormView.getObjectsContainerSID();
                case "PANEL":
                    return DefaultFormView.getPanelContainerSID();
                case "TOOLBARBOX":
                    return DefaultFormView.getToolbarBoxContainerSID();
                case "TOOLBARLEFT":
                    return DefaultFormView.getToolbarLeftContainerSID();
                case "TOOLBARRIGHT":
                    return DefaultFormView.getToolbarRightContainerSID();
                case "TOOLBAR":
                    return DefaultFormView.getToolbarContainerSID();
            }
        } else if (componentSelector.getGroupSingleSelectorType() != null) {
            String groupObjectSID = getGroupObjectSID(componentSelector);
            
            String groupSID = null;
            LSFGroupSelector groupSelector = componentSelector.getGroupSelector();
            if(groupSelector != null)
                groupSID = groupSelector.getGroupUsage().getName();

            return DefaultFormView.getPropertyGroupContainerSID(groupObjectSID, groupSID);
        } else if (componentSelector.getComponentUsage() != null) {
            return componentSelector.getComponentUsage().getSimpleName().getName();
        }
        return null;
    }

    private static String getGroupObjectSID(LSFComponentSelector componentSelector) {
        LSFTreeGroupSelector treeGroupSelector = componentSelector.getTreeGroupSelector();
        if(treeGroupSelector != null)
            return FormView.getTreeSID(treeGroupSelector.getTreeGroupUsage().getName());
        else {
            LSFGroupObjectSelector groupObjectSelector = componentSelector.getGroupObjectSelector();
            if(groupObjectSelector != null)
                return groupObjectSelector.getGroupObjectUsage().getName();
        }
        return null;
    }

}
