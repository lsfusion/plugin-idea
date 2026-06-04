package com.lsfusion.inspections;

import com.intellij.codeInsight.daemon.impl.CollectHighlightsUtil;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.ProblemDescriptorImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.actions.ShowErrorsAction;
import com.lsfusion.lang.LSFErrorLevel;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.LSFReplaceFix;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.psi.impl.LSFLocalDataPropertyDefinitionImpl;
import com.lsfusion.lang.psi.impl.LSFPropertyStatementImpl;
import com.lsfusion.lang.psi.impl.LSFPropertyUsageImpl;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.util.LSFStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class LSFProblemsVisitor {

    public static void analyze(@NotNull PsiFile file) {
        try {
            for (PsiElement element : CollectHighlightsUtil.getElementsInRange(file.getViewProvider().getPsi(LSFLanguage.INSTANCE), 0, file.getTextLength())) {
                if (element instanceof LSFSimpleNameWithCaption && !LSFReferenceAnnotator.isInMetaUsage(element) && !LSFReferenceAnnotator.isInMetaDecl(element)) {
                    visitLSFSimpleNameWithCaption(null, (LSFSimpleNameWithCaption) element, true);
                } else if (element instanceof LSFAssignActionPropertyDefinitionBody) {
                    visitLSFAssignActionPropertyDefinitionBody(null, (LSFAssignActionPropertyDefinitionBody) element, true);
//                } else if (element instanceof LSFPrintActionPropertyDefinitionBody) {
//                    visitLSFPrintActionPropertyDefinitionBody(null, (LSFPrintActionPropertyDefinitionBody) element, true);
                }
            }
        } catch (Exception ignored) {
        }
    }

    static void visitLSFSimpleNameWithCaption(ProblemsHolder holder, LSFSimpleNameWithCaption element, boolean warningsSearchMode) {
        PsiElement parent = element.getParent();
        if (parent instanceof LSFPropertyDeclaration || parent instanceof LSFActionDeclaration || parent instanceof LSFGroupStatement || parent instanceof LSFClassDecl) {
            LSFDeclaration objectDecl = PsiTreeUtil.getParentOfType(element, LSFDeclaration.class);
            if (objectDecl != null && objectDecl.getNameIdentifier() != null && !hasShortCut(objectDecl) &&
                    ReferencesSearch.search(objectDecl.getNameIdentifier(), objectDecl.getUseScope(), true).findFirst() == null) {

                LSFSimpleName simpleName = element.getSimpleName();
                String warningText = getWarningText(parent);
                if (warningsSearchMode) {
                    ShowErrorsAction.showErrorMessage(simpleName, warningText, LSFErrorLevel.WARNING);
                } else if (holder != null) {
                    holder.registerProblem(new ProblemDescriptorImpl(simpleName, simpleName, warningText, null, ProblemHighlightType.LIKE_UNUSED_SYMBOL, false, null, true));
                }
            }
        }
    }

    private static boolean hasShortCut(LSFDeclaration objectDecl) {
        if(objectDecl instanceof LSFPropertyStatementImpl) {
            LSFNonEmptyActionOptions propertyOptions = ((LSFPropertyStatementImpl) objectDecl).getNonEmptyActionOptions();
            if(propertyOptions != null) {
                for(LSFAsEditActionSetting editAction : propertyOptions.getAsEditActionSettingList()) {
                    LSFFormEventType formEventType = editAction.getFormEventType();
                    if(formEventType != null && formEventType.getContextMenuEventType() != null)
                        return true;
                }
            }
        }
        return false;
    }


    private static String getWarningText(PsiElement element) {
        String warningText;
        if (element instanceof LSFPropertyDeclaration) {
            warningText = "Unused Property";
        } else if (element instanceof LSFActionDeclaration) {
            warningText = "Unused Action";
        } else if (element instanceof LSFGroupStatement) {
            warningText = "Unused Group Statement";
        } else {
            warningText = "Unused Class";
        }
        return warningText;
    }

    static void visitLSFAssignActionPropertyDefinitionBody(ProblemsHolder holder, LSFAssignActionPropertyDefinitionBody element, boolean warningsSearchMode) {
        LSFPropertyUsageImpl propertyUsage = (LSFPropertyUsageImpl) element.getFirstChild().getFirstChild();
        if (propertyUsage != null) {
            LSFPropDeclaration declaration = propertyUsage.resolveDecl();
            if (declaration != null) {
                if (declaration instanceof LSFPropertyStatementImpl) {
                    LSFClassSet leftClass = declaration.resolveValueClass();
                    List<LSFPropertyExpression> rightPropertyExpressionList = element.getPropertyExpressionList();
                    if (!rightPropertyExpressionList.isEmpty()) {
                        LSFClassSet rightClass = LSFExClassSet.fromEx(rightPropertyExpressionList.get(0).resolveValueClass(false));
                        if (leftClass != null && rightClass != null) {
                            if (!leftClass.isAssignable(rightClass))
                                showTypeMismatchWarning(holder, element, rightClass, leftClass, warningsSearchMode);
                        }
                    }
                } else if (declaration instanceof LSFLocalDataPropertyDefinitionImpl) {
                    LSFClassName className = ((LSFLocalDataPropertyDefinitionImpl) declaration).getClassName();
                    if (className != null) {
                        LSFClassSet leftClass = LSFPsiImplUtil.resolveClass(className);
                        List<LSFPropertyExpression> rightPropertyExpressionList = element.getPropertyExpressionList();
                        if (!rightPropertyExpressionList.isEmpty()) {
                            LSFClassSet rightClass = LSFExClassSet.fromEx(rightPropertyExpressionList.get(0).resolveValueClass(false));
                            if (leftClass != null && rightClass != null && !leftClass.isAssignable(rightClass))
                                showTypeMismatchWarning(holder, element, rightClass, leftClass, warningsSearchMode);
                        }
                    }
                }
            }
        }
    }

//    static void visitLSFPrintActionPropertyDefinitionBody(ProblemsHolder holder, LSFPrintActionPropertyDefinitionBody element, boolean warningsSearchMode) {
//        LSFFormUsage formUsage = element.getFormUsage();
//        LSFFormActionObjectList formActionObjectList = element.getFormActionObjectList();
//        if (formActionObjectList != null && formUsage != null) {
//
//            //get objects in PRINT
//            List<String> formObjects = new ArrayList<>();
//            List<LSFFormActionObjectUsage> formActionObjectUsageList = formActionObjectList.getFormActionObjectUsageList();
//            for (LSFFormActionObjectUsage formActionObject : formActionObjectUsageList) {
//                LSFSimpleName objectUsage = formActionObject.getObjectUsage().getSimpleName();
//                formObjects.add(objectUsage.getText());
//            }
//
//            //get objects in FORM declaration
//            LSFFormDeclaration formDecl = formUsage.resolveDecl();
//            if (formDecl != null) {
//                PsiElement formStatement = formDecl.getParent();
//                if (formStatement != null && formStatement instanceof LSFFormStatement) {
//                    for (LSFFormGroupObjectsList groupObjectsList : ((LSFFormStatement) formStatement).getFormGroupObjectsListList()) {
//                        for (LSFFormGroupObjectDeclaration formGroupObjectDecl : groupObjectsList.getFormGroupObjectDeclarationList()) {
//
//                            boolean isPanel = false;
//                            for (LSFFormGroupObjectViewType formGroupObjectViewType : formGroupObjectDecl.getFormGroupObjectOptions().getFormGroupObjectViewTypeList()) {
//                                if(!isPanel)
//                                    isPanel = formGroupObjectViewType.getClassViewType().getText().equals("PANEL");
//                            }
//
//                            List<LSFFormObjectDeclaration> formObjectDecls = new ArrayList<>();
//                            LSFFormMultiGroupObjectDeclaration formMultiGroupObjectDecl = formGroupObjectDecl.getFormCommonGroupObject().getFormMultiGroupObjectDeclaration();
//                            if(formMultiGroupObjectDecl != null) {
//                                formObjectDecls.addAll(formMultiGroupObjectDecl.getFormObjectDeclarationList());
//                            }
//                            LSFFormSingleGroupObjectDeclaration formSingleGroupObjectDecl = formGroupObjectDecl.getFormCommonGroupObject().getFormSingleGroupObjectDeclaration();
//                            if(formSingleGroupObjectDecl != null) {
//                                formObjectDecls.add(formSingleGroupObjectDecl.getFormObjectDeclaration());
//                            }
//                            for (LSFFormObjectDeclaration formObjectDecl : formObjectDecls) {
//                                LSFSimpleName formObject = formObjectDecl.getSimpleName();
//                                if (formObject != null) {
//                                    if (formObjects.contains(formObject.getText())) {
//                                        if (!isPanel)
//                                            createObjectShouldBeInPanelWarning(holder, element, formObject, warningsSearchMode);
//                                    } else {
//                                        if (isPanel)
//                                            createNoRequiredObjectWarning(holder, element, formObject, warningsSearchMode);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    private static void showTypeMismatchWarning(ProblemsHolder holder, PsiElement element, LSFClassSet class1, LSFClassSet class2, boolean warningsSearchMode) {
        String message = String.format("Type mismatch: unsafe cast %s to %s", class1.getCanonicalName(), class2.getCanonicalName());
        createWeakWarning(holder, element, message);
    }

//    private static void createObjectShouldBeInPanelWarning(ProblemsHolder holder, PsiElement element, LSFSimpleName objectUsage, boolean warningsSearchMode) {
//        String message = String.format("Object %s in PRINT should be PANEL", objectUsage.getText());
//        createWeakWarning(holder, element, message);
//    }
//
//    private static void createNoRequiredObjectWarning(ProblemsHolder holder, PsiElement element, LSFSimpleName objectUsage, boolean warningsSearchMode) {
//        String message = String.format("No required object %s in PRINT", objectUsage.getText());
//        createWeakWarning(holder, element, message);
//    }

    private static void createWeakWarning(ProblemsHolder holder, PsiElement element, String message) {
        if (holder == null) {
            ShowErrorsAction.showErrorMessage(element, message, LSFErrorLevel.WARNING);
        } else {
            holder.registerProblem(new ProblemDescriptorImpl(element, element, message, null, ProblemHighlightType.WEAK_WARNING, false, null, true));
        }
    }

    public interface DeprecationConsumer {
        void accept(PsiElement element, String version, String text, LSFReplaceFix fix);
    }

    public static void visitDeprecations(PsiElement element, DeprecationConsumer sink) {
        if (element instanceof LSFGroupObjectTreeSingleSelectorType) {
            String text = element.getText();
            if ("USERFILTER".equals(text)) {
                reportReplace(element, "5.2", "6.0", "FILTERS", sink);
            } else if ("GRIDBOX".equals(text)) {
                reportReplace(element, "5.2", "6.0", "GRID", sink);
            }
        } else if (element instanceof LSFCustomOptionsLiteral) {
            if ("HEADER".equals(element.getText())) {
                reportReplace(element, "5.2", "6.0", "OPTIONS", sink);
            }
        } else if (element instanceof LSFAutorefreshLiteral) {
            reportWarning(element, "5.2", "6.0", "Use EVENTS ON SCHEDULE PERIOD n formRefresh() instead", sink);
        } else if (element instanceof LSFDrawRoot) {
            reportWarning(element, "5.2", "6.0", "", sink);
        } else if (element instanceof LSFWindowType) {
            if (!"NATIVE".equals(element.getText())) {
                reportWarning(element, "5.2", "6.0", "Ignore until 6.0", sink);
            }
        } else if (element instanceof LSFSetObjectPropertyStatement) {
            visitSetObjectPropertyDeprecations((LSFSetObjectPropertyStatement) element, sink);
        } else if (element instanceof LSFUserFiltersDeclaration) {
            PsiElement userFilters = ((LSFUserFiltersDeclaration) element).getUserFilters();
            if (userFilters != null) {
                reportWarning(userFilters, "7.0", "Use FILTERS ... USER instead", sink);
            }
        } else if (element instanceof LSFSeekObjectActionPropertyDefinitionBody) {
            PsiElement firstChild = element.getFirstChild();
            if (firstChild != null && firstChild.getNode().getElementType() == LSFTypes.SEEK) {
                reportWarning(firstChild, "7.0", "use ACTIVATE instead", sink);
            }
        } else if (element instanceof LSFObjectPropertyDefinition) {
            PsiElement firstChild = element.getFirstChild();
            if (firstChild != null && firstChild.getNode().getElementType() == LSFTypes.VALUE) {
                reportWarning(firstChild, "7.0", "use ACTIVE instead", sink);
            }
        } else if (element instanceof LSFNewThreadActionPropertyDefinitionBody) {
            visitNewThreadDeprecations((LSFNewThreadActionPropertyDefinitionBody) element, sink);
        }
    }

    private static void visitNewThreadDeprecations(LSFNewThreadActionPropertyDefinitionBody o, DeprecationConsumer sink) {
        PsiElement toKeyword = null;
        for (PsiElement child = o.getFirstChild(); child != null; child = child.getNextSibling()) {
            IElementType type = child.getNode().getElementType();
            if (type == LSFTypes.CONNECTION) {
                reportWarning(child, "7.0", "use NEWEXECUTOR { ... } CLIENT conn NOWAIT instead", sink);
                return;
            }
            if (type == LSFTypes.TO) {
                toKeyword = child;
            }
        }
        // `NEWTHREAD a TO p;` as notification id is legacy - modern spelling is `NEWTHREAD a CLIENT p;`. If the inner
        // action contains a RETURN, `TO p` is the new result-capture form (valid); otherwise it is the legacy form.
        if (toKeyword != null) {
            LSFActionPropertyDefinitionBody inner = o.getActionPropertyDefinitionBody();
            if (inner != null && PsiTreeUtil.findChildOfType(inner, LSFReturnActionPropertyDefinitionBody.class) == null) {
                reportWarning(toKeyword, "7.0", "use CLIENT instead (TO as notification id is deprecated; TO is now reserved for capturing RETURN value of the inner action)", sink);
            }
        }
    }

    private static void visitSetObjectPropertyDeprecations(LSFSetObjectPropertyStatement o, DeprecationConsumer sink) {
        String text = o.getText();
        if (text == null || !text.contains("=")) {
            return;
        }
        String property = text.substring(0, text.indexOf("=")).trim();
        switch (property) {
            case "columns": reportReplace(o, "5.2", "6.0", "lines", sink); break;
            case "type": reportWarning(o, "5.2", "6.0", "Use 'horizontal', 'tabbed', 'lines' instead", sink); break;
            case "toolTip": reportReplace(o, "5.2", "6.0", "tooltip", sink); break;
            case "editOnSingleClick": reportReplace(o, "5.2", "6.0", "changeOnSingleClick", sink); break;
            case "valueAlignment": reportReplace(o, "6.0", "8.0", "valueAlignmentHorz", sink); break;
            case "showGroup": reportReplace(o, "6.0", "8.0", "showViews", sink); break;
            case "autoSize": reportWarning(o, "6.0", "Earlier versions: ignore this warning", sink); break;
            case "changeKeyPriority": reportWarning(o, "6.0", "7.0", "Use parameter 'priority' in 'changeKey' instead", sink); break;
            case "changeMousePriority": reportWarning(o, "6.0", "7.0", "Use parameter 'priority' in 'changeMouse' instead", sink); break;
            case "expandOnClick": reportWarning(o, "6.2", "7.0", "This will be default behaviour", sink); break;
            case "panelCaptionVertical": reportReplace(o, "6.0", "8.0", "captionVertical", sink); break;
            case "panelCaptionLast": reportReplace(o, "6.2", "8.0", "captionLast", sink); break;
            case "panelCaptionAlignment": reportReplace(o, "6.2", "8.0", "captionAlignmentHorz", sink); break;
            case "imagePath": reportReplace(o, "6.2", "8.0", "image", sink); break;
            case "headerHeight": reportReplace(o, "6.2", "8.0", "captionHeight", sink); break;
            case "defaultCompare": reportDefaultCompareDeprecation(o, sink); break;
        }
    }

    private static final List<String> supportedDefaultCompares = Arrays.asList("=", ">", "<", ">=", "<=", "!=", "=*", "=@");
    private static final List<String> supportedDefaultCompares5 = Arrays.asList("EQUALS", "GREATER", "LESS", "GREATER_EQUALS", "LESS_EQUALS", "NOT_EQUALS", "LIKE", "CONTAINS");

    private static void reportDefaultCompareDeprecation(LSFSetObjectPropertyStatement o, DeprecationConsumer sink) {
        LSFComponentPropertyValue element = o.getComponentPropertyValue();
        if (element == null || "NULL".equals(element.getText())) {
            return;
        }
        String defaultCompare = LSFStringUtils.unquote(element.getText());
        int idx = supportedDefaultCompares5.indexOf(defaultCompare);
        if (idx >= 0 && !supportedDefaultCompares.contains(defaultCompare)) { // v5 symbolic name -> deprecated since 5.2
            String replacement = supportedDefaultCompares.get(idx);
            sink.accept(element, "5.2",
                    String.format("Deprecated since version 5.2, removed in version 6.0. Use '%s' instead", replacement),
                    new LSFReplaceFix(element, defaultCompare, replacement));
        }
    }

    private static void reportWarning(PsiElement element, String version, String removedVersion, String comment, DeprecationConsumer sink) {
        sink.accept(element, version, String.format("Deprecated since version %s, removed in version %s. %s", version, removedVersion, comment), null);
    }

    private static void reportWarning(PsiElement element, String version, String comment, DeprecationConsumer sink) {
        sink.accept(element, version, String.format("Deprecated since version %s. %s", version, comment), null);
    }

    private static void reportReplace(PsiElement element, String version, String removedVersion, String replacement, DeprecationConsumer sink) {
        // for option statements `name = value` swap only the name (text before '='), keeping the value
        String elementText = element.getText();
        int eq = elementText.indexOf('=');
        String oldToken = (eq >= 0 ? elementText.substring(0, eq) : elementText).trim();
        sink.accept(element, version, String.format("Deprecated since version %s, removed in version %s. Use '%s' instead", version, removedVersion, replacement),
                new LSFReplaceFix(element, oldToken, replacement));
    }
}