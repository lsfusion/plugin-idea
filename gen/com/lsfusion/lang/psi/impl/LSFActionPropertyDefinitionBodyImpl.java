// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public class LSFActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFActionPropertyDefinitionBody {

  public LSFActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFActivateActionPropertyDefinitionBody getActivateActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFActivateActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFActiveFormActionPropertyDefinitionBody getActiveFormActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFActiveFormActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFApplyActionPropertyDefinitionBody getApplyActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFApplyActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFAssignActionPropertyDefinitionBody getAssignActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFAssignActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFAsyncUpdateActionPropertyDefinitionBody getAsyncUpdateActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFAsyncUpdateActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFCancelActionPropertyDefinitionBody getCancelActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFCancelActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFCaseActionPropertyDefinitionBody getCaseActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFCaseActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFChangeClassActionPropertyDefinitionBody getChangeClassActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFChangeClassActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFCloseFormActionPropertyDefinitionBody getCloseFormActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFCloseFormActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFCollapseGroupObjectActionPropertyDefinitionBody getCollapseGroupObjectActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFCollapseGroupObjectActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFConfirmActionPropertyDefinitionBody getConfirmActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFConfirmActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFDeleteActionPropertyDefinitionBody getDeleteActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFDeleteActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFDialogActionPropertyDefinitionBody getDialogActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFDialogActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFDrillDownActionPropertyDefinitionBody getDrillDownActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFDrillDownActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFEmailActionPropertyDefinitionBody getEmailActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFEmailActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFEvalActionPropertyDefinitionBody getEvalActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFEvalActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFExecActionPropertyDefinitionBody getExecActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFExecActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFExpandCollapseActionPropertyDefinitionBody getExpandCollapseActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFExpandCollapseActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFExpandGroupObjectActionPropertyDefinitionBody getExpandGroupObjectActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFExpandGroupObjectActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFExportActionPropertyDefinitionBody getExportActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFExportActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFExportDataActionPropertyDefinitionBody getExportDataActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFExportDataActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFExternalActionPropertyDefinitionBody getExternalActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFExternalActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFFilterActionPropertyDefinitionBody getFilterActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFFilterActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFForActionPropertyDefinitionBody getForActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFForActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFFormActionPropertyDefinitionBody getFormActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFFormActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFIfActionPropertyDefinitionBody getIfActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFIfActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFImportActionPropertyDefinitionBody getImportActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFImportActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFImportFormActionPropertyDefinitionBody getImportFormActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFImportFormActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFInputActionPropertyDefinitionBody getInputActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFInputActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFInternalActionPropertyDefinitionBody getInternalActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFInternalActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFListActionPropertyDefinitionBody getListActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFListActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFMessageActionPropertyDefinitionBody getMessageActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFMessageActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFMultiActionPropertyDefinitionBody getMultiActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFMultiActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFNewActionPropertyDefinitionBody getNewActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFNewActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFNewExecutorActionPropertyDefinitionBody getNewExecutorActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFNewExecutorActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFNewSessionActionPropertyDefinitionBody getNewSessionActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFNewSessionActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFNewThreadActionPropertyDefinitionBody getNewThreadActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFNewThreadActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFNewWhereActionPropertyDefinitionBody getNewWhereActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFNewWhereActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFOrderActionPropertyDefinitionBody getOrderActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFOrderActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFPrintActionPropertyDefinitionBody getPrintActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFPrintActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFReadActionPropertyDefinitionBody getReadActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFReadActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFReadFilterActionPropertyDefinitionBody getReadFilterActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFReadFilterActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFReadOrderActionPropertyDefinitionBody getReadOrderActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFReadOrderActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFRecalculateActionPropertyDefinitionBody getRecalculateActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFRecalculateActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFRequestActionPropertyDefinitionBody getRequestActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFRequestActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFSeekObjectActionPropertyDefinitionBody getSeekObjectActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFSeekObjectActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFTerminalFlowActionPropertyDefinitionBody getTerminalFlowActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFTerminalFlowActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFTryActionPropertyDefinitionBody getTryActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFTryActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFWhileActionPropertyDefinitionBody getWhileActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFWhileActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFWriteActionPropertyDefinitionBody getWriteActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFWriteActionPropertyDefinitionBody.class);
  }

  @Override
  public Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params) {
    return LSFPsiImplUtil.inferActionParamClasses(this, params);
  }

}
