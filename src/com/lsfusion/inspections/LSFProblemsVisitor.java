package com.lsfusion.inspections;

import com.intellij.codeInsight.daemon.impl.CollectHighlightsUtil;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.ProblemDescriptorImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.actions.ShowErrorsAction;
import com.lsfusion.lang.LSFErrorLevel;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.impl.*;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LSFProblemsVisitor {

    public static void analyze(@NotNull PsiFile file) {
        try {
            for (PsiElement element : CollectHighlightsUtil.getElementsInRange(file.getViewProvider().getPsi(LSFLanguage.INSTANCE), 0, file.getTextLength())) {
                if (element instanceof LSFSimpleNameWithCaption && !LSFReferenceAnnotator.isInMetaUsage(element)) {
                    visitLSFSimpleNameWithCaption(null, (LSFSimpleNameWithCaption) element, true);
                } else if (element instanceof LSFAssignActionPropertyDefinitionBody) {
                    visitLSFAssignActionPropertyDefinitionBody(null, (LSFAssignActionPropertyDefinitionBody) element, true);
                } else if (element instanceof LSFPrintActionPropertyDefinitionBody) {
                    visitLSFPrintActionPropertyDefinitionBody(null, (LSFPrintActionPropertyDefinitionBody) element, true);
                }
            }
        } catch (Exception ignored) {
        }
    }

    static void visitLSFSimpleNameWithCaption(ProblemsHolder holder, LSFSimpleNameWithCaption element, boolean warningsSearchMode) {
        PsiElement parent = element.getParent();
        if (parent instanceof LSFActionOrPropDeclaration || parent instanceof LSFGroupStatement || parent instanceof LSFClassDecl) {
            LSFDeclaration objectDecl = PsiTreeUtil.getParentOfType(element, LSFDeclaration.class);
            if (objectDecl != null && objectDecl.getNameIdentifier() != null && !hasShortCut(objectDecl) &&
                    ReferencesSearch.search(objectDecl.getNameIdentifier(), element.getUseScope(), true).findFirst() == null) {

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
        if(objectDecl != null && objectDecl instanceof LSFPropertyStatementImpl) {
            LSFNonEmptyPropertyOptions propertyOptions = ((LSFPropertyStatementImpl) objectDecl).getNonEmptyPropertyOptions();
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

    static void visitLSFPrintActionPropertyDefinitionBody(ProblemsHolder holder, LSFPrintActionPropertyDefinitionBody element, boolean warningsSearchMode) {
        LSFFormUsage formUsage = element.getFormUsage();
        LSFFormActionObjectList formActionObjectList = element.getFormActionObjectList();
        if (formActionObjectList != null && formUsage != null) {

            //get objects in PRINT
            List<String> formObjects = new ArrayList<>();
            List<LSFFormActionObjectUsage> formActionObjectUsageList = formActionObjectList.getFormActionObjectUsageList();
            for (LSFFormActionObjectUsage formActionObject : formActionObjectUsageList) {
                LSFSimpleName objectUsage = formActionObject.getObjectUsage().getSimpleName();
                formObjects.add(objectUsage.getText());
            }

            //get objects in FORM declaration
            LSFFormDeclaration formDecl = formUsage.resolveDecl();
            if (formDecl != null) {
                PsiElement formStatement = formDecl.getParent();
                if (formStatement != null && formStatement instanceof LSFFormStatement) {
                    for (LSFFormGroupObjectsList groupObjectsList : ((LSFFormStatement) formStatement).getFormGroupObjectsListList()) {
                        for (LSFFormGroupObjectDeclaration formGroupObjectDecl : groupObjectsList.getFormGroupObjectDeclarationList()) {

                            boolean isPanel = false;
                            for (LSFFormGroupObjectViewType formGroupObjectViewType : formGroupObjectDecl.getFormGroupObjectOptions().getFormGroupObjectViewTypeList()) {
                                if(!isPanel)
                                    isPanel = formGroupObjectViewType.getClassViewType().getText().equals("PANEL");
                            }

                            List<LSFFormObjectDeclaration> formObjectDecls = new ArrayList<>();
                            LSFFormMultiGroupObjectDeclaration formMultiGroupObjectDecl = formGroupObjectDecl.getFormCommonGroupObject().getFormMultiGroupObjectDeclaration();
                            if(formMultiGroupObjectDecl != null) {
                                formObjectDecls.addAll(formMultiGroupObjectDecl.getFormObjectDeclarationList());
                            }
                            LSFFormSingleGroupObjectDeclaration formSingleGroupObjectDecl = formGroupObjectDecl.getFormCommonGroupObject().getFormSingleGroupObjectDeclaration();
                            if(formSingleGroupObjectDecl != null) {
                                formObjectDecls.add(formSingleGroupObjectDecl.getFormObjectDeclaration());
                            }
                            for (LSFFormObjectDeclaration formObjectDecl : formObjectDecls) {
                                LSFSimpleName formObject = formObjectDecl.getSimpleName();
                                if (formObject != null) {
                                    if (formObjects.contains(formObject.getText())) {
                                        if (!isPanel)
                                            createObjectShouldBeInPanelWarning(holder, element, formObject, warningsSearchMode);
                                    } else {
                                        if (isPanel)
                                            createNoRequiredObjectWarning(holder, element, formObject, warningsSearchMode);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void showTypeMismatchWarning(ProblemsHolder holder, PsiElement element, LSFClassSet class1, LSFClassSet class2, boolean warningsSearchMode) {
        String message = String.format("Type mismatch: unsafe cast %s to %s", class1.getCanonicalName(), class2.getCanonicalName());
        createWeakWarning(holder, element, message);
    }

    private static void createObjectShouldBeInPanelWarning(ProblemsHolder holder, PsiElement element, LSFSimpleName objectUsage, boolean warningsSearchMode) {
        String message = String.format("Object %s in PRINT should be PANEL", objectUsage.getText());
        createWeakWarning(holder, element, message);
    }

    private static void createNoRequiredObjectWarning(ProblemsHolder holder, PsiElement element, LSFSimpleName objectUsage, boolean warningsSearchMode) {
        String message = String.format("No required object %s in PRINT", objectUsage.getText());
        createWeakWarning(holder, element, message);
    }

    private static void createWeakWarning(ProblemsHolder holder, PsiElement element, String message) {
        if (holder == null) {
            ShowErrorsAction.showErrorMessage(element, message, LSFErrorLevel.WARNING);
        } else {
            holder.registerProblem(new ProblemDescriptorImpl(element, element, message, null, ProblemHighlightType.WEAK_WARNING, false, null, true));
        }
    }
}