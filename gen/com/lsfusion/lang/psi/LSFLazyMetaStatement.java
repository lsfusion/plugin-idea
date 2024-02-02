// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LSFLazyMetaStatement extends PsiElement {

  @NotNull
  List<LSFAspectStatement> getAspectStatementList();

  @NotNull
  List<LSFClassStatement> getClassStatementList();

  @NotNull
  List<LSFConstraintStatement> getConstraintStatementList();

  @NotNull
  List<LSFDesignStatement> getDesignStatementList();

  @NotNull
  List<LSFEmptyStatement> getEmptyStatementList();

  @NotNull
  List<LSFEventStatement> getEventStatementList();

  @NotNull
  List<LSFExplicitInterfaceActStatement> getExplicitInterfaceActStatementList();

  @NotNull
  List<LSFExplicitInterfacePropertyStatement> getExplicitInterfacePropertyStatementList();

  @NotNull
  List<LSFFollowsStatement> getFollowsStatementList();

  @NotNull
  List<LSFFormStatement> getFormStatementList();

  @NotNull
  List<LSFGlobalEventStatement> getGlobalEventStatementList();

  @NotNull
  List<LSFGroupStatement> getGroupStatementList();

  @NotNull
  List<LSFIndexStatement> getIndexStatementList();

  @NotNull
  List<LSFInternalStatement> getInternalStatementList();

  @NotNull
  List<LSFLoggableStatement> getLoggableStatementList();

  @NotNull
  List<LSFMetaCodeStatement> getMetaCodeStatementList();

  @NotNull
  List<LSFNavigatorStatement> getNavigatorStatementList();

  @NotNull
  List<LSFOverrideActionStatement> getOverrideActionStatementList();

  @NotNull
  List<LSFOverridePropertyStatement> getOverridePropertyStatementList();

  @NotNull
  List<LSFShowDepStatement> getShowDepStatementList();

  @NotNull
  List<LSFStubStatement> getStubStatementList();

  @NotNull
  List<LSFTableStatement> getTableStatementList();

  @NotNull
  List<LSFWindowStatement> getWindowStatementList();

  @NotNull
  List<LSFWriteWhenStatement> getWriteWhenStatementList();

}
