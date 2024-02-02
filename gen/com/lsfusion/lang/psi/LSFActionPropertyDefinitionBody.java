// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ActionExpression;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public interface LSFActionPropertyDefinitionBody extends ActionExpression {

  @Nullable
  LSFActivateActionPropertyDefinitionBody getActivateActionPropertyDefinitionBody();

  @Nullable
  LSFActiveFormActionPropertyDefinitionBody getActiveFormActionPropertyDefinitionBody();

  @Nullable
  LSFApplyActionPropertyDefinitionBody getApplyActionPropertyDefinitionBody();

  @Nullable
  LSFAssignActionPropertyDefinitionBody getAssignActionPropertyDefinitionBody();

  @Nullable
  LSFAsyncUpdateActionPropertyDefinitionBody getAsyncUpdateActionPropertyDefinitionBody();

  @Nullable
  LSFCancelActionPropertyDefinitionBody getCancelActionPropertyDefinitionBody();

  @Nullable
  LSFCaseActionPropertyDefinitionBody getCaseActionPropertyDefinitionBody();

  @Nullable
  LSFChangeClassActionPropertyDefinitionBody getChangeClassActionPropertyDefinitionBody();

  @Nullable
  LSFCloseFormActionPropertyDefinitionBody getCloseFormActionPropertyDefinitionBody();

  @Nullable
  LSFCollapseGroupObjectActionPropertyDefinitionBody getCollapseGroupObjectActionPropertyDefinitionBody();

  @Nullable
  LSFConfirmActionPropertyDefinitionBody getConfirmActionPropertyDefinitionBody();

  @Nullable
  LSFDeleteActionPropertyDefinitionBody getDeleteActionPropertyDefinitionBody();

  @Nullable
  LSFDialogActionPropertyDefinitionBody getDialogActionPropertyDefinitionBody();

  @Nullable
  LSFDrillDownActionPropertyDefinitionBody getDrillDownActionPropertyDefinitionBody();

  @Nullable
  LSFEmailActionPropertyDefinitionBody getEmailActionPropertyDefinitionBody();

  @Nullable
  LSFEvalActionPropertyDefinitionBody getEvalActionPropertyDefinitionBody();

  @Nullable
  LSFExecActionPropertyDefinitionBody getExecActionPropertyDefinitionBody();

  @Nullable
  LSFExpandCollapseActionPropertyDefinitionBody getExpandCollapseActionPropertyDefinitionBody();

  @Nullable
  LSFExpandGroupObjectActionPropertyDefinitionBody getExpandGroupObjectActionPropertyDefinitionBody();

  @Nullable
  LSFExportActionPropertyDefinitionBody getExportActionPropertyDefinitionBody();

  @Nullable
  LSFExportDataActionPropertyDefinitionBody getExportDataActionPropertyDefinitionBody();

  @Nullable
  LSFExternalActionPropertyDefinitionBody getExternalActionPropertyDefinitionBody();

  @Nullable
  LSFFilterActionPropertyDefinitionBody getFilterActionPropertyDefinitionBody();

  @Nullable
  LSFForActionPropertyDefinitionBody getForActionPropertyDefinitionBody();

  @Nullable
  LSFFormActionPropertyDefinitionBody getFormActionPropertyDefinitionBody();

  @Nullable
  LSFIfActionPropertyDefinitionBody getIfActionPropertyDefinitionBody();

  @Nullable
  LSFImportActionPropertyDefinitionBody getImportActionPropertyDefinitionBody();

  @Nullable
  LSFImportFormActionPropertyDefinitionBody getImportFormActionPropertyDefinitionBody();

  @Nullable
  LSFInputActionPropertyDefinitionBody getInputActionPropertyDefinitionBody();

  @Nullable
  LSFInternalActionPropertyDefinitionBody getInternalActionPropertyDefinitionBody();

  @Nullable
  LSFListActionPropertyDefinitionBody getListActionPropertyDefinitionBody();

  @Nullable
  LSFMessageActionPropertyDefinitionBody getMessageActionPropertyDefinitionBody();

  @Nullable
  LSFMultiActionPropertyDefinitionBody getMultiActionPropertyDefinitionBody();

  @Nullable
  LSFNewActionPropertyDefinitionBody getNewActionPropertyDefinitionBody();

  @Nullable
  LSFNewExecutorActionPropertyDefinitionBody getNewExecutorActionPropertyDefinitionBody();

  @Nullable
  LSFNewSessionActionPropertyDefinitionBody getNewSessionActionPropertyDefinitionBody();

  @Nullable
  LSFNewThreadActionPropertyDefinitionBody getNewThreadActionPropertyDefinitionBody();

  @Nullable
  LSFNewWhereActionPropertyDefinitionBody getNewWhereActionPropertyDefinitionBody();

  @Nullable
  LSFOrderActionPropertyDefinitionBody getOrderActionPropertyDefinitionBody();

  @Nullable
  LSFPrintActionPropertyDefinitionBody getPrintActionPropertyDefinitionBody();

  @Nullable
  LSFReadActionPropertyDefinitionBody getReadActionPropertyDefinitionBody();

  @Nullable
  LSFReadFilterActionPropertyDefinitionBody getReadFilterActionPropertyDefinitionBody();

  @Nullable
  LSFReadOrderActionPropertyDefinitionBody getReadOrderActionPropertyDefinitionBody();

  @Nullable
  LSFRecalculateActionPropertyDefinitionBody getRecalculateActionPropertyDefinitionBody();

  @Nullable
  LSFRequestActionPropertyDefinitionBody getRequestActionPropertyDefinitionBody();

  @Nullable
  LSFSeekObjectActionPropertyDefinitionBody getSeekObjectActionPropertyDefinitionBody();

  @Nullable
  LSFTerminalFlowActionPropertyDefinitionBody getTerminalFlowActionPropertyDefinitionBody();

  @Nullable
  LSFTryActionPropertyDefinitionBody getTryActionPropertyDefinitionBody();

  @Nullable
  LSFWhileActionPropertyDefinitionBody getWhileActionPropertyDefinitionBody();

  @Nullable
  LSFWriteActionPropertyDefinitionBody getWriteActionPropertyDefinitionBody();

  Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);

}
