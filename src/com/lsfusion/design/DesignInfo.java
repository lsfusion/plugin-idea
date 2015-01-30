package com.lsfusion.design;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import com.lsfusion.design.model.ComponentView;
import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.proxy.ViewProxyUtil;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormPropertyDrawUsageImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;

import javax.help.UnsupportedOperationException;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DesignInfo {
    public final LSFFormDeclaration formDecl;

    public FormView formView;

    public DesignInfo(LSFFormDeclaration formDecl, LSFFile lsfFile) {
        this.formDecl = formDecl;

        Query<LSFFormExtend> lsfFormExtends = LSFGlobalResolver.findExtendElements(formDecl, LSFStubElementTypes.EXTENDFORM, lsfFile);

        List<LSFFormExtend> formExtends = new ArrayList<LSFFormExtend>(lsfFormExtends.findAll());
        Collections.sort(formExtends, REQUIRES_COMPARATOR);

        FormEntity formEntity = new FormEntity(lsfFile);

        for (LSFFormExtend formExtend : formExtends) {
            formEntity.extendForm(formExtend);
        }

        Collection<PsiReference> refs = ReferencesSearch.search(formDecl.getNameIdentifier(), lsfFile.getRequireScope()).findAll();

        List<LSFFormUsage> formUsages = new ArrayList<LSFFormUsage>();
        for (PsiReference ref : refs) {
            if (ref instanceof LSFFormUsage) {
                formUsages.add((LSFFormUsage) ref);
            }
        }
        Collections.sort(formUsages, REQUIRES_COMPARATOR);

        for (LSFFormUsage ref : formUsages) {
            PsiElement parent = ref.getParent();
            if (parent instanceof LSFDesignDeclaration) {
                formView = ((LSFDesignDeclaration) parent).getFromDefaultStatement() != null ? DefaultFormView.create(formEntity) : FormView.create(formEntity);
            }
            if (formView != null && (parent instanceof LSFDesignDeclaration || parent instanceof LSFExtendDesignDeclaration)) {
                LSFDesignStatement designStatement = (LSFDesignStatement) parent.getParent();

                processComponentBody(formView.getMainContainer(), designStatement.getComponentBody());
            }
        }
        if (formView == null) { // если дизайн вообще не прописан
            formView = DefaultFormView.create(formEntity);
        }
    }

    private static final Comparator<LSFElement> REQUIRES_COMPARATOR = new Comparator<LSFElement>() {
        @Override
        public int compare(LSFElement o1, LSFElement o2) {
            LSFModuleDeclaration decl1 = o1.getLSFFile().getModuleDeclaration();
            if (decl1 != null) {
                LSFModuleDeclaration decl2 = o2.getLSFFile().getModuleDeclaration();
                if (decl2 != null) {
                    if (decl1.requires(decl2)) {
                        return 1;
                    } else if (decl2.requires(decl1)) {
                        return -1;
                    }
                }
            }

            String name1;
            String name2;

            if (o1 instanceof LSFFormExtend && o2 instanceof LSFFormExtend) {
                name1 = ((LSFFormExtend) o1).getGlobalName();
                name2 = ((LSFFormExtend) o2).getGlobalName();
            } else if (o1 instanceof LSFFormUsage && o2 instanceof LSFFormUsage) {
                name1 = ((LSFFormUsage) o1).getNameRef();
                name2 = ((LSFFormUsage) o2).getNameRef();
            } else {
                throw new UnsupportedOperationException("Uncomparable classes");
            }

            return StringUtil.naturalCompare(name1, name2);
        }
    };

    public String getFormCaption() {
        return formView.getCaption();
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
            if (componentStatement.getSetupComponentStatement() != null) {
                LSFSetupComponentStatement statement = componentStatement.getSetupComponentStatement();

                String componentSID = getComponentSID(statement.getComponentSelector(), formView);
                ComponentView componentView = formView.getComponentBySID(componentSID);

                if (componentView != null) {
                    processComponentBody(componentView, statement.getComponentBody());
                }
            } else if (componentStatement.getNewComponentStatement() != null) {
                LSFNewComponentStatement statement = componentStatement.getNewComponentStatement();
                String name = statement.getComponentStubDecl().getComponentDecl().getName();
                ContainerView container = formView.createContainer(null, name);

                LSFComponentInsertPosition insertPositionSelector = statement.getComponentInsertPosition();
                addComponent(container, (ContainerView) parentComponent, insertPositionSelector, formView);

                processComponentBody(container, statement.getComponentBody());
            } else if (componentStatement.getAddComponentStatement() != null) {
                LSFAddComponentStatement statement = componentStatement.getAddComponentStatement();
                LSFComponentSelector componentSelector = statement.getComponentSelector();
                if (componentSelector != null) {
                    String name = getComponentSID(componentSelector, formView);
                    if (name != null) {
                        ComponentView component = formView.getComponentBySID(name);

                        if (component != null) {
                            LSFComponentInsertPosition insertPositionSelector = statement.getComponentInsertPosition();
                            addComponent(component, (ContainerView) parentComponent, insertPositionSelector, formView);

                            processComponentBody(component, statement.getComponentBody());
                        }
                    }
                }
            } else if (componentStatement.getSetObjectPropertyStatement() != null) {
                LSFSetObjectPropertyStatement statement = componentStatement.getSetObjectPropertyStatement();
                if (statement != null) {
                    String propertyName = statement.getFirstChild().getText();

                    LSFComponentPropertyValue propertyValue = statement.getComponentPropertyValue();
                    Object propertyValueObject = getPropertyValue(propertyValue);

                    if (parentComponent != null) {
                        ViewProxyUtil.setObjectProperty(parentComponent.equals(formView.getMainContainer()) ? formView : parentComponent, propertyName, propertyValueObject);
                    }
                }
            } else if (componentStatement.getRemoveComponentStatement() != null) {
                LSFRemoveComponentStatement statement = componentStatement.getRemoveComponentStatement();
                if (statement.getComponentSelector() != null) {
                    String name = getComponentSID(statement.getComponentSelector(), formView);
                    ComponentView component = formView.getComponentBySID(name);
                    if (component != null) {
                        formView.removeComponent(component, statement.getRemoveCascade() != null);
                    }
                }
            }
        }
    }

    private Object getPropertyValue(LSFComponentPropertyValue valueStatement) {
        if (valueStatement == null) {
            return null;
        }
        
        if (valueStatement.getColorLiteral() != null) {
            LSFColorLiteral colorLiteral = valueStatement.getColorLiteral();
            if (!colorLiteral.getUintLiteralList().isEmpty()) {
                List<LSFUintLiteral> literalList = colorLiteral.getUintLiteralList();
                return new Color(Integer.decode(literalList.get(0).getText()), Integer.decode(literalList.get(1).getText()), Integer.decode(literalList.get(2).getText()));
            } else {
                return Color.decode(colorLiteral.getFirstChild().getText());
            }
        } else if (valueStatement.getStringLiteral() != null) {
            return valueStatement.getStringLiteral().getValue();
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

    private void addComponent(ComponentView component, ContainerView container, LSFComponentInsertPosition insertPositionSelector, FormView form) {
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
            LSFFormPropertyDrawUsageImpl usage = (LSFFormPropertyDrawUsageImpl) componentSelector.getPropertySelector().getFormPropertyDrawUsage();
            if (usage.getAliasUsage() != null) {
                return usage.getAliasUsage().getSimpleName().getName();
            } else {
                String name = usage.getSimpleName().getName();
                LSFObjectUsageList objectUsageList = usage.getObjectUsageList();
                
                assert objectUsageList != null;
                
                LSFNonEmptyObjectUsageList usageList = objectUsageList.getNonEmptyObjectUsageList();
                if (usageList != null) {
                    name += "_";
                    List<LSFObjectUsage> objectUsages = usageList.getObjectUsageList();
                    for (LSFObjectUsage objectUsage : objectUsages) {
                        name += objectUsage.getName();
                        if (objectUsages.indexOf(objectUsage) < objectUsages.size() - 1) {
                            name += "_";
                        }
                    }
                }

                return name;
            }
        } else if (componentSelector.getComponentUsage() != null) {
            return componentSelector.getComponentUsage().getMultiCompoundID().getName();
        }
        return null;
    }
}
