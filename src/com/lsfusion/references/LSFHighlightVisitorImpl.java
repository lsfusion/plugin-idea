/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lsfusion.references;

import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightVisitorImpl;
import com.intellij.lang.Language;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiResolveHelper;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.actions.ShowErrorsAction;
import com.lsfusion.actions.ToggleHighlightWarningsAction;
import com.lsfusion.lang.LSFErrorLevel;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.psi.impl.LSFLocalDataPropertyDefinitionImpl;
import com.lsfusion.lang.psi.impl.LSFPropertyStatementImpl;
import com.lsfusion.lang.psi.impl.LSFPropertyUsageImpl;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LSFHighlightVisitorImpl extends HighlightVisitorImpl {

    @SuppressWarnings("unused")
    public LSFHighlightVisitorImpl(@NotNull PsiResolveHelper resolveHelper) {
        super(resolveHelper);
    }

    @Override
    public boolean suitableForFile(@NotNull PsiFile file) {
        return file instanceof LSFFile || super.suitableForFile(file);
    }

    public static void analyze(@NotNull PsiFile file) {
        analyzeLSF(file, null, true);
    }

    @Override
    public boolean analyze(@NotNull PsiFile file, boolean updateWholeFile, @NotNull HighlightInfoHolder holder, @NotNull Runnable action) {
        super.analyze(file, updateWholeFile, holder, action);
        if (ToggleHighlightWarningsAction.isHighlightWarningsEnabled(file.getProject())) {
            analyzeLSF(file, holder, false);
        }
        return true;
    }

    private static void analyzeLSF(PsiFile file, HighlightInfoHolder holder, boolean warningsSearchMode) {
        try {

            final FileViewProvider viewProvider = file.getViewProvider();
            for (Language language : viewProvider.getLanguages()) {
                for (PsiElement element : CollectHighlightsUtil.getElementsInRange(viewProvider.getPsi(language), 0, file.getTextLength())) {
                    if (element instanceof LSFSimpleNameWithCaption && !LSFReferenceAnnotator.isInMetaUsage(element)) {
                        visitLSFSimpleNameWithCaption(holder, element, warningsSearchMode);
                    } else if(element instanceof LSFAssignActionPropertyDefinitionBody) {
                        visitLSFAssignActionPropertyDefinitionBody(holder, (LSFAssignActionPropertyDefinitionBody) element, warningsSearchMode);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void visitLSFSimpleNameWithCaption(HighlightInfoHolder holder, PsiElement element, boolean warningsSearchMode) {
        PsiElement parent = element.getParent();
        if(parent instanceof LSFPropertyDeclaration || parent instanceof LSFGroupStatement || parent instanceof LSFClassDecl) {
            LSFDeclaration objectDecl = PsiTreeUtil.getParentOfType(element, LSFDeclaration.class);

            if (objectDecl != null && objectDecl.getNameIdentifier() != null && ReferencesSearch.search(objectDecl.getNameIdentifier(), element.getUseScope(), true).findFirst() == null) {

                String warningText = getWarningText(parent);
                if (warningsSearchMode) {
                    ShowErrorsAction.showErrorMessage(element, warningText, LSFErrorLevel.WARNING);
                } else if (holder != null) {
                    HighlightInfo highlightInfo = UnusedSymbolUtil.createUnusedSymbolInfo(element, warningText, HighlightInfoType.UNUSED_SYMBOL);
                    holder.add(highlightInfo);
                }
            }
        }
    }

    private static String getWarningText(PsiElement element) {
        String warningText;
        if (element instanceof LSFPropertyDeclaration) {
            warningText = "Unused Property";
        } else if (element instanceof LSFGroupStatement) {
            warningText = "Unused Group Statement";
        } else {
            warningText = "Unused Class";
        }
        return warningText;
    }

    private static void visitLSFAssignActionPropertyDefinitionBody(HighlightInfoHolder holder, LSFAssignActionPropertyDefinitionBody element, boolean warningsSearchMode) {
        LSFPropertyUsageImpl propertyUsage = (LSFPropertyUsageImpl) element.getFirstChild().getFirstChild();
        if (propertyUsage != null) {
            LSFPropDeclaration declaration = propertyUsage.resolveDecl();
            if (declaration != null) {
                if (declaration instanceof LSFPropertyStatementImpl) {
                    LSFClassSet leftClass = declaration.resolveValueClass();
                    List<LSFPropertyExpression> rightPropertyExpressionList = element.getPropertyExpressionList();
                    if (!rightPropertyExpressionList.isEmpty()) {
                        LSFClassSet rightClass = LSFExClassSet.fromEx(rightPropertyExpressionList.get(0).resolveValueClass(false));
                        if(leftClass != null && rightClass != null) {
                            if (!leftClass.isAssignable(rightClass))
                                showTypeMismatchHighlight(holder, element, rightClass, leftClass, warningsSearchMode);
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
                                showTypeMismatchHighlight(holder, element, rightClass, leftClass, warningsSearchMode);
                        }
                    }
                }
            }
        }
    }

    private static void showTypeMismatchHighlight(HighlightInfoHolder holder, LSFAssignActionPropertyDefinitionBody element, LSFClassSet class1, LSFClassSet class2, boolean warningsSearchMode) {
        String message = String.format("Type mismatch: unsafe cast %s to %s", class1.getCanonicalName(), class2.getCanonicalName());
        if (warningsSearchMode) {
            ShowErrorsAction.showErrorMessage(element, message, LSFErrorLevel.WARNING);
        } else if (holder != null) {
            HighlightInfo highlightInfo = HighlightInfo.newHighlightInfo(HighlightInfoType.WEAK_WARNING).range(element).descriptionAndTooltip(
                    String.format("Type mismatch: unsafe cast %s to %s", class1.getCanonicalName(), class2.getCanonicalName())).create();
            holder.add(highlightInfo);
        }
    }
}