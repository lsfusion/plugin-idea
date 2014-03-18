package com.lsfusion.design;

import com.intellij.find.findUsages.DefaultFindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.Query;
import com.lsfusion.design.model.ComponentView;
import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class DesignInfo {
    public final LSFFormDeclaration formDecl;

    public FormView formView;

    public DesignInfo(LSFFormDeclaration formDecl) {
        this.formDecl = formDecl;

        Query<LSFFormExtend> lsfFormExtends = LSFGlobalResolver.findExtendElements(formDecl, LSFStubElementTypes.EXTENDFORM, formDecl.getLSFFile());

        formView = new DefaultFormView();
        for (LSFFormExtend formExtend : lsfFormExtends.findAll()) {
            formView.extendForm(formExtend);
            DefaultFindUsagesHandlerFactory fact = new DefaultFindUsagesHandlerFactory();
            Collection<PsiReference> refs = fact.createFindUsagesHandler(formDecl.getNameIdentifier(), false).findReferencesToHighlight(formDecl.getNameIdentifier(), formDecl.getLSFFile().getRequireScope());

            for (PsiReference ref : refs) {
                if (ref instanceof LSFFormUsage) {
                    PsiElement parent = ((LSFFormUsage) ref).getParent();
                    if (parent instanceof LSFDesignDeclaration || parent instanceof LSFExtendDesignDeclaration) {
                        LSFDesignStatement designStatement = (LSFDesignStatement) parent.getParent();

                        processComponentBody(formView, formView.getMainContainer(), designStatement.getComponentBody());
                    }
                }
            }
        }

        System.out.println();
    }

    public String getFormCaption() {
        return formDecl.getCaption();
    }

    public String getFormSID() {
        return formDecl.getGlobalName();
    }

    private void processComponentBody(FormView form, ComponentView parentComponent, LSFComponentBody componentBody) {
        LSFComponentBlockStatement componentBlockStatement = componentBody.getComponentBlockStatement();
        if (componentBlockStatement == null) {
            return;
        }

        List<LSFComponentStatement> componentStatements = componentBlockStatement.getComponentStatementList();

        for (LSFComponentStatement componentStatement : componentStatements) {
            if (componentStatement.getSetupComponentStatement() != null) {
                LSFSetupComponentStatement statement = componentStatement.getSetupComponentStatement();

                String componentSID = getComponentSID(statement.getComponentSelector(), form);
                ComponentView componentView = form.getComponentBySID(componentSID);

                processComponentBody(form, componentView, statement.getComponentBody());
            } else if (componentStatement.getNewComponentStatement() != null) {
                LSFNewComponentStatement statement = componentStatement.getNewComponentStatement();
                String name = statement.getComponentStubDecl().getComponentDecl().getName();
                ContainerView container = form.createContainer(null, name);

                LSFComponentInsertPositionSelector insertPositionSelector = statement.getComponentInsertPositionSelector();
                addComponent(container, (ContainerView) parentComponent, insertPositionSelector, form);

                processComponentBody(form, container, statement.getComponentBody());
            } else if (componentStatement.getAddComponentStatement() != null) {
                LSFAddComponentStatement statement = componentStatement.getAddComponentStatement();
                String name = getComponentSID(statement.getComponentSelector(), form);
                ContainerView component = form.getContainerBySID(name);

                LSFComponentInsertPositionSelector insertPositionSelector = statement.getComponentInsertPositionSelector();
                addComponent(component, (ContainerView) parentComponent, insertPositionSelector, form);

                processComponentBody(form, component, statement.getComponentBody());
            } else if (componentStatement.getSetObjectPropertyStatement() != null) {
                LSFSetObjectPropertyStatement statement = componentStatement.getSetObjectPropertyStatement();
                String propertyName = statement.getFirstChild().getText();

                LSFComponentPropertyValue propertyValue = statement.getComponentPropertyValue();
                Object propertyValueObject = getPropertyValue(propertyValue);

                if (parentComponent != null) {
                    parentComponent.setProperty(propertyName, propertyValueObject);
                }
            }
        }
    }

    private Object getPropertyValue(LSFComponentPropertyValue valueStatement) {
        if (valueStatement.getColorLiteral() != null) {
            LSFColorLiteral colorLiteral = valueStatement.getColorLiteral();
            if (!colorLiteral.getUintLiteralList().isEmpty()) {
                List<LSFUintLiteral> literalList = colorLiteral.getUintLiteralList();
                return new Color(Integer.decode(literalList.get(0).getText()), Integer.decode(literalList.get(1).getText()), Integer.decode(literalList.get(2).getText()));
            } else {
                return Color.decode(colorLiteral.getFirstChild().getText());
            }
        } else if (valueStatement.getStringLiteral() != null) {
            return valueStatement.getStringLiteral().getText();
        } else if (valueStatement.getIntLiteral() != null) {
            return Integer.decode(valueStatement.getIntLiteral().getText());
        } else if (valueStatement.getDoubleLiteral() != null) {
            return Double.parseDouble(valueStatement.getDoubleLiteral().getText());
        } else if (valueStatement.getDimensionLiteral() != null) {
            List<LSFIntLiteral> literalList = valueStatement.getDimensionLiteral().getIntLiteralList();
            return new Dimension(Integer.decode(literalList.get(0).getText()), Integer.decode(literalList.get(1).getText()));
        } else if (valueStatement.getBooleanLiteral() != null) {
            return "TRUE".equals(valueStatement.getBooleanLiteral().getText());
        } else if (valueStatement.getBoundsIntLiteral() != null) {
            return null; //??????????
        } else if (valueStatement.getBoundsDoubleLiteral() != null) {
            return null; //??????????
        } else if (valueStatement.getContainerTypeLiteral() != null) {
            return ContainerType.valueOf(valueStatement.getContainerTypeLiteral().getText());
        } else if (valueStatement.getFlexAlignmentLiteral() != null) {
            return FlexAlignment.valueOf(valueStatement.getFlexAlignmentLiteral().getText());
        }
        return null;
    }

    private void addComponent(ComponentView component, ContainerView container, LSFComponentInsertPositionSelector insertPositionSelector, FormView form) {
        LSFInPositionComponent inPositionComponent = insertPositionSelector.getInPositionComponent();
        if (inPositionComponent != null) {
            container = form.getContainerBySID(getComponentSID(inPositionComponent.getComponentSelector(), form));
        }
        LSFComponentSelector neighbour = insertPositionSelector.getComponentSelector();
        if (neighbour != null) {
            LSFInsertRelativePositionLiteral insertRelativePositionLiteral = insertPositionSelector.getInsertRelativePositionLiteral();
            String relativePosition = insertRelativePositionLiteral.getText();
            ComponentView componentView = form.getComponentBySID(getComponentSID(neighbour, form));
            if ("BEFORE".equals(relativePosition)) {
                container.addBefore(component, componentView);
            } else {
                container.addAfter(component, componentView);
            }
        } else if ("FIRST".equals(insertPositionSelector.getText())) {
            container.addFirst(component);
        } else {
            container.add(component);
        }
    }

    private String getComponentSID(LSFComponentSelector componentSelector, FormView form) {
        if (componentSelector.getComponentSelector() != null) {
            String componentSID = getComponentSID(componentSelector.getComponentSelector(), form);
            ComponentView componentView = form.getComponentBySID(componentSID);
            return componentView.getParent().getSID();
        } else if (componentSelector.getPropertySelector() != null) {
            return componentSelector.getPropertySelector().getFormPropertyDrawUsage().getSimpleName().getName();
        } else {
            return componentSelector.getComponentUsage().getMultiCompoundID().getName();
        }
    }
}
