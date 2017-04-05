package com.lsfusion.references;

import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.lang.Language;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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

import java.util.List;

/**
 * based on DefaultHighlightVisitor
 */
public class LSFHighlightVisitor implements HighlightVisitor, DumbAware {
  private AnnotationHolderImpl myAnnotationHolder;

  private final Project myProject;
  private final boolean myHighlightErrorElements;
  private final boolean myRunAnnotators;
  private static HighlightInfoHolder myHolder;
  private final boolean myBatchMode;
  private final CachedAnnotators myCachedAnnotators;

  @SuppressWarnings("UnusedDeclaration")
  LSFHighlightVisitor(@NotNull Project project, @NotNull CachedAnnotators cachedAnnotators) {
    this(project, true, true, false, cachedAnnotators);
  }

  private LSFHighlightVisitor(@NotNull Project project,
                              boolean highlightErrorElements,
                              boolean runAnnotators,
                              boolean batchMode,
                              @NotNull CachedAnnotators cachedAnnotators) {
    myProject = project;
    myHighlightErrorElements = highlightErrorElements;
    myRunAnnotators = runAnnotators;
    myCachedAnnotators = cachedAnnotators;
    myBatchMode = batchMode;
  }

  @Override
  public boolean suitableForFile(@NotNull final PsiFile file) {
    return (file instanceof LSFFile && ToggleHighlightWarningsAction.isHighlightWarningsEnabled(file.getProject()));
  }

  @Override
  public boolean analyze(@NotNull final PsiFile file,
                         final boolean updateWholeFile,
                         @NotNull final HighlightInfoHolder holder,
                         @NotNull final Runnable action) {
    myHolder = holder;
    myAnnotationHolder = new AnnotationHolderImpl(holder.getAnnotationSession(), myBatchMode);
    try {
      action.run();
    }
    finally {
      myAnnotationHolder.clear();
      myAnnotationHolder = null;
      myHolder = null;
    }
    return true;
  }

  public static void analyze(@NotNull PsiFile file) {
    try {
      final FileViewProvider viewProvider = file.getViewProvider();
      for (Language language : viewProvider.getLanguages()) {
        for (PsiElement element : CollectHighlightsUtil.getElementsInRange(viewProvider.getPsi(language), 0, file.getTextLength())) {
          visitLSFElement(element, true);
        }
      }
    } catch (Exception ignored) {
    }
  }

  @Override
  public void visit(@NotNull PsiElement element) {
    visitLSFElement(element, false);
  }

  private static void visitLSFElement(PsiElement element, boolean warningsSearchMode) {
    if (element instanceof LSFSimpleNameWithCaption && !LSFReferenceAnnotator.isInMetaUsage(element)) {
      visitLSFSimpleNameWithCaption(myHolder, element, warningsSearchMode);
    } else if(element instanceof LSFAssignActionPropertyDefinitionBody) {
      visitLSFAssignActionPropertyDefinitionBody(myHolder, (LSFAssignActionPropertyDefinitionBody) element, warningsSearchMode);
    }
  }

  private static void visitLSFSimpleNameWithCaption(HighlightInfoHolder holder, PsiElement element, boolean warningsSearchMode) {
    PsiElement parent = element.getParent();
    if(parent instanceof LSFPropertyDeclaration || parent instanceof LSFGroupStatement || parent instanceof LSFClassDecl) {
      LSFDeclaration objectDecl = PsiTreeUtil.getParentOfType(element, LSFDeclaration.class);

      if (objectDecl != null && objectDecl.getNameIdentifier() != null && ReferencesSearch.search(objectDecl.getNameIdentifier(), objectDecl.getUseScope(), true).findFirst() == null) {

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

  @SuppressWarnings("CloneDoesntCallSuperClone")
  @Override
  @NotNull
  public HighlightVisitor clone() {
    return new LSFHighlightVisitor(myProject, myHighlightErrorElements, myRunAnnotators, myBatchMode, myCachedAnnotators);
  }

  @Override
  public int order() {
    return 2;
  }
}